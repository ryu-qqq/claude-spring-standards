"""
Java AST Parser using Tree-sitter
AESA-125: Tree-sitter Java 파서 통합

Tree-sitter를 사용하여 Java 코드를 AST로 파싱하고 분석하는 기능 제공
"""

from typing import Optional, Iterator
from dataclasses import dataclass
import tree_sitter_java as ts_java
from tree_sitter import Language, Parser, Node, Tree


# Singleton instance
_java_parser: Optional["JavaParser"] = None


@dataclass
class ParsedClass:
    """파싱된 Java 클래스 정보"""

    name: str
    node: Node
    modifiers: list[str]
    annotations: list[str]
    extends: Optional[str] = None
    implements: list[str] = None
    fields: list["ParsedField"] = None
    methods: list["ParsedMethod"] = None

    def __post_init__(self):
        if self.implements is None:
            self.implements = []
        if self.fields is None:
            self.fields = []
        if self.methods is None:
            self.methods = []


@dataclass
class ParsedField:
    """파싱된 필드 정보"""

    name: str
    type: str
    node: Node
    modifiers: list[str]
    annotations: list[str]
    line_number: int


@dataclass
class ParsedMethod:
    """파싱된 메서드 정보"""

    name: str
    return_type: str
    node: Node
    modifiers: list[str]
    annotations: list[str]
    parameters: list[tuple[str, str]]  # [(type, name), ...]
    line_number: int
    body: Optional[str] = None


@dataclass
class ParsedAnnotation:
    """파싱된 어노테이션 정보"""

    name: str
    node: Node
    line_number: int
    arguments: Optional[str] = None


class JavaParser:
    """Tree-sitter 기반 Java 파서"""

    def __init__(self):
        """파서 초기화"""
        self._language = Language(ts_java.language())
        self._parser = Parser(self._language)

    def parse(self, code: str) -> Tree:
        """Java 코드를 AST로 파싱"""
        return self._parser.parse(bytes(code, "utf-8"))

    def get_root(self, code: str) -> Node:
        """루트 노드 반환"""
        tree = self.parse(code)
        return tree.root_node

    def find_nodes(self, root: Node, node_type: str) -> Iterator[Node]:
        """특정 타입의 모든 노드 찾기"""
        if root.type == node_type:
            yield root
        for child in root.children:
            yield from self.find_nodes(child, node_type)

    def find_classes(self, code: str) -> list[ParsedClass]:
        """모든 클래스 선언 찾기"""
        root = self.get_root(code)
        classes = []

        for node in self.find_nodes(root, "class_declaration"):
            parsed = self._parse_class(node, code)
            if parsed:
                classes.append(parsed)

        # Record 클래스도 포함
        for node in self.find_nodes(root, "record_declaration"):
            parsed = self._parse_record(node, code)
            if parsed:
                classes.append(parsed)

        return classes

    def find_interfaces(self, code: str) -> list[ParsedClass]:
        """모든 인터페이스 선언 찾기"""
        root = self.get_root(code)
        interfaces = []

        for node in self.find_nodes(root, "interface_declaration"):
            parsed = self._parse_interface(node, code)
            if parsed:
                interfaces.append(parsed)

        return interfaces

    def find_annotations(self, code: str) -> list[ParsedAnnotation]:
        """모든 어노테이션 찾기"""
        root = self.get_root(code)
        annotations = []

        for node in self.find_nodes(root, "marker_annotation"):
            name = self._get_annotation_name(node, code)
            if name:
                annotations.append(
                    ParsedAnnotation(
                        name=name,
                        node=node,
                        line_number=node.start_point[0] + 1,
                    )
                )

        for node in self.find_nodes(root, "annotation"):
            name = self._get_annotation_name(node, code)
            args = self._get_annotation_arguments(node, code)
            if name:
                annotations.append(
                    ParsedAnnotation(
                        name=name,
                        node=node,
                        line_number=node.start_point[0] + 1,
                        arguments=args,
                    )
                )

        return annotations

    def find_fields(self, code: str) -> list[ParsedField]:
        """모든 필드 선언 찾기"""
        root = self.get_root(code)
        fields = []

        for node in self.find_nodes(root, "field_declaration"):
            parsed = self._parse_field(node, code)
            if parsed:
                fields.extend(parsed)

        return fields

    def find_methods(self, code: str) -> list[ParsedMethod]:
        """모든 메서드 선언 찾기"""
        root = self.get_root(code)
        methods = []

        for node in self.find_nodes(root, "method_declaration"):
            parsed = self._parse_method(node, code)
            if parsed:
                methods.append(parsed)

        return methods

    def find_imports(self, code: str) -> list[str]:
        """모든 import 문 찾기 (static import 포함)"""
        root = self.get_root(code)
        imports = []

        for node in self.find_nodes(root, "import_declaration"):
            import_text = self._get_node_text(node, code)
            # "import xxx.yyy.zzz;" 또는 "import static xxx.yyy.zzz;" 에서 패키지 경로만 추출
            if import_text.startswith("import static "):
                # static import: "import static xxx.yyy.zzz;" → "xxx.yyy.zzz"
                path = import_text[14:].rstrip(";").strip()
                imports.append(path)
            elif import_text.startswith("import "):
                # 일반 import: "import xxx.yyy.zzz;" → "xxx.yyy.zzz"
                path = import_text[7:].rstrip(";").strip()
                imports.append(path)

        return imports

    def find_package(self, code: str) -> Optional[str]:
        """패키지 선언 찾기"""
        root = self.get_root(code)

        for node in self.find_nodes(root, "package_declaration"):
            text = self._get_node_text(node, code)
            if text.startswith("package "):
                return text[8:].rstrip(";").strip()

        return None

    def has_annotation(self, code: str, annotation_name: str) -> bool:
        """특정 어노테이션 존재 여부 확인"""
        annotations = self.find_annotations(code)
        # @Data, Data 모두 매칭
        target = annotation_name.lstrip("@")
        return any(a.name == target or a.name == f"@{target}" for a in annotations)

    def get_annotation_on_class(self, class_node: Node, code: str) -> list[str]:
        """클래스에 붙은 어노테이션 목록 반환"""
        annotations = []

        # 클래스 선언 바로 앞의 modifiers 노드에서 어노테이션 찾기
        for child in class_node.children:
            if child.type == "modifiers":
                for mod_child in child.children:
                    if mod_child.type in ("marker_annotation", "annotation"):
                        name = self._get_annotation_name(mod_child, code)
                        if name:
                            annotations.append(name)

        return annotations

    def check_method_chaining(self, code: str) -> list[tuple[int, str]]:
        """메서드 체이닝 (Law of Demeter 위반) 검사

        Returns:
            list of (line_number, code_snippet) for violations
        """
        root = self.get_root(code)
        violations = []

        for node in self.find_nodes(root, "method_invocation"):
            # 체이닝: a.b().c() 패턴 탐지
            chain_depth = self._get_chain_depth(node)
            if chain_depth >= 2:  # 2단계 이상 체이닝
                line = node.start_point[0] + 1
                snippet = self._get_node_text(node, code)
                violations.append((line, snippet))

        return violations

    def _get_chain_depth(self, node: Node) -> int:
        """메서드 체이닝 깊이 계산

        obj.getA().getB().getC() → depth = 3
        obj.a.b.getC() → depth = 1 (field_access는 체이닝 아님)
        obj.getA().b.getC() → depth = 2 (method_invocation만 카운트)
        """
        depth = 0
        current = node

        while current:
            if current.type == "method_invocation":
                depth += 1
                # 첫 번째 자식에서 다음 체인 요소 찾기
                next_node = self._find_next_chain_node(current)
                if next_node:
                    current = next_node
                    continue
            elif current.type == "field_access":
                # field_access 내부에서 method_invocation 탐색
                next_node = self._find_next_chain_node(current)
                if next_node:
                    current = next_node
                    continue
            break

        return depth

    def _find_next_chain_node(self, node: Node) -> Optional[Node]:
        """체인의 다음 노드(method_invocation 또는 field_access) 찾기"""
        if not node.children:
            return None

        first_child = node.children[0]

        # 직접 method_invocation이면 반환
        if first_child.type == "method_invocation":
            return first_child

        # field_access면 그 안의 method_invocation을 재귀적으로 탐색
        if first_child.type == "field_access":
            # field_access 내부에 method_invocation이 있는지 확인
            for child in first_child.children:
                if child.type == "method_invocation":
                    return child
            # method_invocation이 없으면 field_access 자체 반환 (추가 탐색용)
            return first_child

        return None

    def _parse_class(self, node: Node, code: str) -> Optional[ParsedClass]:
        """클래스 노드 파싱"""
        name = None
        modifiers = []
        annotations = []
        extends = None
        implements = []

        for child in node.children:
            if child.type == "identifier":
                name = self._get_node_text(child, code)
            elif child.type == "modifiers":
                for mod in child.children:
                    if mod.type in (
                        "public",
                        "private",
                        "protected",
                        "static",
                        "final",
                        "abstract",
                    ):
                        modifiers.append(self._get_node_text(mod, code))
                    elif mod.type in ("marker_annotation", "annotation"):
                        ann_name = self._get_annotation_name(mod, code)
                        if ann_name:
                            annotations.append(ann_name)
            elif child.type == "superclass":
                # extends 처리
                for sc in child.children:
                    if sc.type == "type_identifier":
                        extends = self._get_node_text(sc, code)
            elif child.type == "super_interfaces":
                # implements 처리
                for si in child.children:
                    if si.type == "type_list":
                        for t in si.children:
                            if t.type == "type_identifier":
                                implements.append(self._get_node_text(t, code))

        if not name:
            return None

        parsed = ParsedClass(
            name=name,
            node=node,
            modifiers=modifiers,
            annotations=annotations,
            extends=extends,
            implements=implements,
        )

        # 필드와 메서드 파싱
        for child in node.children:
            if child.type == "class_body":
                for body_child in child.children:
                    if body_child.type == "field_declaration":
                        fields = self._parse_field(body_child, code)
                        if fields:
                            parsed.fields.extend(fields)
                    elif body_child.type == "method_declaration":
                        method = self._parse_method(body_child, code)
                        if method:
                            parsed.methods.append(method)

        return parsed

    def _parse_record(self, node: Node, code: str) -> Optional[ParsedClass]:
        """Record 노드 파싱"""
        name = None
        modifiers = []
        annotations = []
        implements = []

        for child in node.children:
            if child.type == "identifier":
                name = self._get_node_text(child, code)
            elif child.type == "modifiers":
                for mod in child.children:
                    if mod.type in (
                        "public",
                        "private",
                        "protected",
                        "static",
                        "final",
                    ):
                        modifiers.append(self._get_node_text(mod, code))
                    elif mod.type in ("marker_annotation", "annotation"):
                        ann_name = self._get_annotation_name(mod, code)
                        if ann_name:
                            annotations.append(ann_name)

        if not name:
            return None

        return ParsedClass(
            name=name,
            node=node,
            modifiers=modifiers,
            annotations=annotations,
            implements=implements,
        )

    def _parse_interface(self, node: Node, code: str) -> Optional[ParsedClass]:
        """인터페이스 노드 파싱"""
        name = None
        modifiers = []
        annotations = []

        for child in node.children:
            if child.type == "identifier":
                name = self._get_node_text(child, code)
            elif child.type == "modifiers":
                for mod in child.children:
                    if mod.type in ("public", "private", "protected", "static"):
                        modifiers.append(self._get_node_text(mod, code))
                    elif mod.type in ("marker_annotation", "annotation"):
                        ann_name = self._get_annotation_name(mod, code)
                        if ann_name:
                            annotations.append(ann_name)

        if not name:
            return None

        return ParsedClass(
            name=name,
            node=node,
            modifiers=modifiers,
            annotations=annotations,
        )

    def _parse_field(self, node: Node, code: str) -> list[ParsedField]:
        """필드 노드 파싱 (한 선언에 여러 필드 가능)"""
        fields = []
        field_type = None
        modifiers = []
        annotations = []

        for child in node.children:
            if child.type == "modifiers":
                for mod in child.children:
                    if mod.type in (
                        "public",
                        "private",
                        "protected",
                        "static",
                        "final",
                        "volatile",
                        "transient",
                    ):
                        modifiers.append(self._get_node_text(mod, code))
                    elif mod.type in ("marker_annotation", "annotation"):
                        ann_name = self._get_annotation_name(mod, code)
                        if ann_name:
                            annotations.append(ann_name)
            elif child.type in (
                "type_identifier",
                "generic_type",
                "array_type",
                "integral_type",
                "floating_point_type",
                "boolean_type",
            ):
                field_type = self._get_node_text(child, code)
            elif child.type == "variable_declarator":
                for vc in child.children:
                    if vc.type == "identifier":
                        name = self._get_node_text(vc, code)
                        if name and field_type:
                            fields.append(
                                ParsedField(
                                    name=name,
                                    type=field_type,
                                    node=node,
                                    modifiers=modifiers.copy(),
                                    annotations=annotations.copy(),
                                    line_number=node.start_point[0] + 1,
                                )
                            )

        return fields

    def _parse_method(self, node: Node, code: str) -> Optional[ParsedMethod]:
        """메서드 노드 파싱"""
        name = None
        return_type = "void"
        modifiers = []
        annotations = []
        parameters = []
        body = None

        for child in node.children:
            if child.type == "identifier":
                name = self._get_node_text(child, code)
            elif child.type == "modifiers":
                for mod in child.children:
                    if mod.type in (
                        "public",
                        "private",
                        "protected",
                        "static",
                        "final",
                        "abstract",
                        "synchronized",
                        "native",
                    ):
                        modifiers.append(self._get_node_text(mod, code))
                    elif mod.type in ("marker_annotation", "annotation"):
                        ann_name = self._get_annotation_name(mod, code)
                        if ann_name:
                            annotations.append(ann_name)
            elif child.type in (
                "type_identifier",
                "generic_type",
                "array_type",
                "void_type",
                "integral_type",
                "floating_point_type",
                "boolean_type",
            ):
                return_type = self._get_node_text(child, code)
            elif child.type == "formal_parameters":
                parameters = self._parse_parameters(child, code)
            elif child.type == "block":
                body = self._get_node_text(child, code)

        if not name:
            return None

        return ParsedMethod(
            name=name,
            return_type=return_type,
            node=node,
            modifiers=modifiers,
            annotations=annotations,
            parameters=parameters,
            line_number=node.start_point[0] + 1,
            body=body,
        )

    def _parse_parameters(self, node: Node, code: str) -> list[tuple[str, str]]:
        """파라미터 목록 파싱"""
        params = []

        for child in node.children:
            if child.type == "formal_parameter":
                param_type = None
                param_name = None

                for pc in child.children:
                    if pc.type in (
                        "type_identifier",
                        "generic_type",
                        "array_type",
                        "integral_type",
                        "floating_point_type",
                        "boolean_type",
                    ):
                        param_type = self._get_node_text(pc, code)
                    elif pc.type == "identifier":
                        param_name = self._get_node_text(pc, code)

                if param_type and param_name:
                    params.append((param_type, param_name))

        return params

    def _get_annotation_name(self, node: Node, code: str) -> Optional[str]:
        """어노테이션 이름 추출"""
        for child in node.children:
            if child.type == "identifier":
                return self._get_node_text(child, code)
        return None

    def _get_annotation_arguments(self, node: Node, code: str) -> Optional[str]:
        """어노테이션 인자 추출"""
        for child in node.children:
            if child.type == "annotation_argument_list":
                return self._get_node_text(child, code)
        return None

    def _get_node_text(self, node: Node, code: str) -> str:
        """노드의 텍스트 추출"""
        return code[node.start_byte : node.end_byte]


def get_java_parser() -> JavaParser:
    """싱글톤 JavaParser 인스턴스 반환"""
    global _java_parser
    if _java_parser is None:
        _java_parser = JavaParser()
    return _java_parser
