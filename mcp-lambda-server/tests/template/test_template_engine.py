"""
Template Engine Tests

AESA-120: Jinja2 템플릿 엔진 구축 검증 테스트
"""

import pytest

from src.template.models import (
    FieldDefinition,
    MethodDefinition,
    TemplateContext,
    GeneratedCode,
)
from src.template.engine import (
    get_template_engine,
    BASE_TEMPLATES,
)


class TestFieldDefinition:
    """FieldDefinition 모델 테스트"""

    def test_field_with_required_fields(self):
        """필수 필드만으로 생성"""
        field = FieldDefinition(name="orderId", type="Long")
        assert field.name == "orderId"
        assert field.type == "Long"
        assert field.access_modifier == "private"
        assert field.is_final is False
        assert field.default_value is None
        assert field.description is None

    def test_field_with_all_fields(self):
        """모든 필드로 생성"""
        field = FieldDefinition(
            name="status",
            type="OrderStatus",
            access_modifier="protected",
            is_final=True,
            default_value="OrderStatus.PENDING",
            description="주문 상태",
        )
        assert field.name == "status"
        assert field.type == "OrderStatus"
        assert field.access_modifier == "protected"
        assert field.is_final is True
        assert field.default_value == "OrderStatus.PENDING"
        assert field.description == "주문 상태"


class TestMethodDefinition:
    """MethodDefinition 모델 테스트"""

    def test_method_with_required_fields(self):
        """필수 필드만으로 생성"""
        method = MethodDefinition(name="execute")
        assert method.name == "execute"
        assert method.return_type == "void"
        assert method.parameters == []
        assert method.access_modifier == "public"
        assert method.is_static is False
        assert method.body is None
        assert method.annotations == []

    def test_method_with_all_fields(self):
        """모든 필드로 생성"""
        method = MethodDefinition(
            name="calculateTotal",
            return_type="Money",
            parameters=[("Order", "order"), ("Discount", "discount")],
            access_modifier="public",
            is_static=False,
            body="return order.getAmount().subtract(discount.getValue());",
            description="총액 계산",
            annotations=["@Override"],
        )
        assert method.name == "calculateTotal"
        assert method.return_type == "Money"
        assert len(method.parameters) == 2
        assert method.body is not None
        assert "@Override" in method.annotations


class TestTemplateContext:
    """TemplateContext 모델 테스트"""

    def test_context_with_required_fields(self):
        """필수 필드만으로 생성"""
        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
        )
        assert ctx.class_name == "OrderAggregate"
        assert ctx.package_name == "com.example.domain.order"
        assert ctx.class_type == "AGGREGATE"
        assert ctx.layer == "DOMAIN"
        assert ctx.fields == []
        assert ctx.methods == []
        assert ctx.imports == []
        assert ctx.annotations == []
        assert ctx.interfaces == []
        assert ctx.extends is None

    def test_context_with_fields_and_methods(self):
        """필드와 메서드 포함"""
        field = FieldDefinition(name="id", type="Long")
        method = MethodDefinition(name="getId", return_type="Long")

        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
            description="주문 애그리거트",
            fields=[field],
            methods=[method],
            imports=["java.time.LocalDateTime"],
            annotations=["@AggregateRoot"],
        )
        assert len(ctx.fields) == 1
        assert len(ctx.methods) == 1
        assert len(ctx.imports) == 1
        assert ctx.description == "주문 애그리거트"


class TestGeneratedCode:
    """GeneratedCode 모델 테스트"""

    def test_generated_code_model(self):
        """생성된 코드 모델"""
        code = GeneratedCode(
            class_name="OrderAggregate",
            class_type="AGGREGATE",
            layer="DOMAIN",
            package_name="com.example.domain.order",
            file_path="src/main/java/com/example/domain/order/OrderAggregate.java",
            code="public class OrderAggregate {}",
            applied_rules=["AGG-001", "AGG-002"],
            warnings=["Consider adding validation"],
        )
        assert code.class_name == "OrderAggregate"
        assert code.class_type == "AGGREGATE"
        assert code.layer == "DOMAIN"
        assert "OrderAggregate.java" in code.file_path
        assert len(code.applied_rules) == 2
        assert len(code.warnings) == 1


class TestBaseTemplates:
    """BASE_TEMPLATES 상수 테스트"""

    def test_all_class_types_have_templates(self):
        """모든 클래스 타입에 템플릿 존재"""
        expected_types = [
            "AGGREGATE",
            "VALUE_OBJECT",
            "DOMAIN_EVENT",
            "DOMAIN_EXCEPTION",
            "USE_CASE",
            "PORT_IN",
            "PORT_OUT",
            "ENTITY",
            "JPA_REPOSITORY",
            "ADAPTER",
            "CONTROLLER",
            "REQUEST_DTO",
            "RESPONSE_DTO",
            "MAPPER",
        ]
        for class_type in expected_types:
            assert class_type in BASE_TEMPLATES, f"Missing template for {class_type}"

    def test_template_structure(self):
        """템플릿 구조 확인 - 템플릿 코드는 문자열"""
        for class_type, template_code in BASE_TEMPLATES.items():
            # BASE_TEMPLATES는 class_type -> template_code 문자열 매핑
            assert isinstance(template_code, str), (
                f"Template for {class_type} should be string"
            )
            # 템플릿은 비어있지 않아야 함
            assert len(template_code) > 0, f"Empty template for {class_type}"
            # 템플릿에는 package 선언이 포함되어야 함
            assert "package" in template_code, (
                f"Template for {class_type} should include package"
            )

    def test_layer_mapping_via_class_type_to_layer(self):
        """CLASS_TYPE_TO_LAYER 매핑 확인"""
        from src.template.engine import CLASS_TYPE_TO_LAYER

        domain_types = ["AGGREGATE", "VALUE_OBJECT", "DOMAIN_EVENT", "DOMAIN_EXCEPTION"]
        application_types = ["USE_CASE", "PORT_IN", "PORT_OUT"]
        persistence_types = ["ENTITY", "JPA_REPOSITORY", "ADAPTER"]
        rest_api_types = ["CONTROLLER", "REQUEST_DTO", "RESPONSE_DTO", "MAPPER"]

        for class_type in domain_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "DOMAIN"

        for class_type in application_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "APPLICATION"

        for class_type in persistence_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "PERSISTENCE"

        for class_type in rest_api_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "REST_API"


class TestTemplateEngine:
    """TemplateEngine 클래스 테스트"""

    def test_singleton_pattern(self):
        """싱글톤 패턴 확인"""
        engine1 = get_template_engine()
        engine2 = get_template_engine()
        assert engine1 is engine2

    def test_has_template(self):
        """템플릿 존재 여부 확인"""
        engine = get_template_engine()
        assert engine.has_template("AGGREGATE") is True
        assert engine.has_template("VALUE_OBJECT") is True
        assert engine.has_template("NONEXISTENT") is False

    def test_get_available_templates(self):
        """사용 가능한 템플릿 목록 조회"""
        engine = get_template_engine()
        templates = engine.get_available_templates()
        assert len(templates) >= 14
        # get_available_templates()는 class_type 문자열 목록 반환
        for template in templates:
            assert isinstance(template, str)
        # 주요 타입들이 포함되어 있는지 확인
        assert "AGGREGATE" in templates
        assert "USE_CASE" in templates
        assert "ENTITY" in templates
        assert "CONTROLLER" in templates


class TestTemplateRendering:
    """템플릿 렌더링 테스트"""

    def test_render_aggregate(self):
        """Aggregate 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
            description="주문 애그리거트",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderAggregate"
        assert result.class_type == "AGGREGATE"
        assert result.layer == "DOMAIN"
        assert "package com.example.domain.order;" in result.code
        assert "public class OrderAggregate" in result.code
        assert len(result.applied_rules) > 0

    def test_render_value_object(self):
        """Value Object 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Money",
            package_name="com.example.domain.common",
            class_type="VALUE_OBJECT",
            layer="DOMAIN",
            fields=[
                FieldDefinition(name="amount", type="BigDecimal", is_final=True),
                FieldDefinition(name="currency", type="Currency", is_final=True),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "Money"
        assert "public final class Money" in result.code
        assert "BigDecimal" in result.code or "amount" in result.code

    def test_render_use_case(self):
        """UseCase 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderUseCase",
            package_name="com.example.application.order",
            class_type="USE_CASE",
            layer="APPLICATION",
        )
        result = engine.render(ctx)

        assert result.class_name == "CreateOrderUseCase"
        assert result.layer == "APPLICATION"
        assert "public class CreateOrderUseCase" in result.code

    def test_render_entity(self):
        """Entity 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderEntity",
            package_name="com.example.persistence.order",
            class_type="ENTITY",
            layer="PERSISTENCE",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderEntity"
        assert result.layer == "PERSISTENCE"
        assert "@Entity" in result.code
        assert "@Table" in result.code

    def test_render_controller(self):
        """Controller 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderController",
            package_name="com.example.api.order",
            class_type="CONTROLLER",
            layer="REST_API",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderController"
        assert result.layer == "REST_API"
        assert "@RestController" in result.code
        assert "@RequestMapping" in result.code

    def test_render_request_dto(self):
        """Request DTO 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderRequest",
            package_name="com.example.api.order.dto",
            class_type="REQUEST_DTO",
            layer="REST_API",
            fields=[
                FieldDefinition(name="customerId", type="Long"),
                FieldDefinition(name="productId", type="Long"),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "CreateOrderRequest"
        assert "public record CreateOrderRequest" in result.code

    def test_render_with_imports(self):
        """import 포함 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
            imports=[
                "java.time.LocalDateTime",
                "java.util.UUID",
            ],
        )
        result = engine.render(ctx)

        assert "import java.time.LocalDateTime;" in result.code
        assert "import java.util.UUID;" in result.code

    def test_render_with_methods(self):
        """메서드 포함 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
            methods=[
                MethodDefinition(
                    name="cancel",
                    return_type="void",
                    description="주문 취소",
                ),
                MethodDefinition(
                    name="calculateTotal",
                    return_type="Money",
                    parameters=[("Discount", "discount")],
                ),
            ],
        )
        result = engine.render(ctx)

        assert "void cancel()" in result.code
        assert "Money calculateTotal(Discount discount)" in result.code

    def test_render_with_annotations(self):
        """어노테이션 포함 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderUseCase",
            package_name="com.example.application.order",
            class_type="USE_CASE",
            layer="APPLICATION",
            annotations=["@Transactional", "@Service"],
        )
        result = engine.render(ctx)

        assert "@Transactional" in result.code
        assert "@Service" in result.code

    def test_render_with_interfaces(self):
        """인터페이스 구현 렌더링"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderPersistenceAdapter",
            package_name="com.example.persistence.order",
            class_type="ADAPTER",
            layer="PERSISTENCE",
            interfaces=["OrderRepository", "OrderQueryPort"],
        )
        result = engine.render(ctx)

        assert "implements OrderRepository, OrderQueryPort" in result.code

    def test_render_with_extends(self):
        """상속 렌더링 - AGGREGATE는 extends 지원"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="SpecialOrder",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
            extends="BaseAggregate",
        )
        result = engine.render(ctx)

        # AGGREGATE 템플릿은 extends 지원
        assert "extends BaseAggregate" in result.code

    def test_render_unknown_template_raises_error(self):
        """알 수 없는 템플릿 타입은 ValueError 발생"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Unknown",
            package_name="com.example",
            class_type="NONEXISTENT",
            layer="UNKNOWN",
        )

        # 알 수 없는 타입은 ValueError 발생
        with pytest.raises(ValueError) as excinfo:
            engine.render(ctx)
        assert "No template found" in str(excinfo.value)


class TestJinja2Filters:
    """Jinja2 커스텀 필터 테스트"""

    def test_capitalize_first(self):
        """첫 글자 대문자화"""
        engine = get_template_engine()
        # 필터 직접 테스트
        filter_func = engine.env.filters["capitalize_first"]
        assert filter_func("orderService") == "OrderService"
        assert filter_func("Order") == "Order"
        assert filter_func("") == ""

    def test_to_snake_case(self):
        """snake_case 변환 - 대문자 앞에 _ 추가"""
        engine = get_template_engine()
        filter_func = engine.env.filters["to_snake_case"]
        assert filter_func("OrderService") == "order_service"
        assert filter_func("orderService") == "order_service"
        # HTTPClient는 각 대문자 앞에 _가 추가됨 (현재 구현 동작)
        result = filter_func("HTTPClient")
        assert result.startswith("h")  # 소문자로 시작
        assert "_" in result  # _ 포함

    def test_to_camel_case(self):
        """camelCase 변환 - snake_case에서"""
        engine = get_template_engine()
        filter_func = engine.env.filters["to_camel_case"]
        assert filter_func("order_service") == "orderService"
        # 대문자 snake_case는 각 부분의 title()을 적용
        result = filter_func("order_data")
        assert result == "orderData"

    def test_to_pascal_case(self):
        """PascalCase 변환 - snake_case에서"""
        engine = get_template_engine()
        filter_func = engine.env.filters["to_pascal_case"]
        assert filter_func("order_service") == "OrderService"
        # _로 구분된 문자열에서 변환
        assert filter_func("order_data_service") == "OrderDataService"

    def test_to_kebab_case(self):
        """kebab-case 변환"""
        engine = get_template_engine()
        filter_func = engine.env.filters["to_kebab_case"]
        assert filter_func("OrderService") == "order-service"
        assert filter_func("orderService") == "order-service"


class TestFilePathGeneration:
    """파일 경로 생성 테스트"""

    def test_file_path_from_package(self):
        """패키지 기반 파일 경로 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderAggregate",
            package_name="com.example.domain.order",
            class_type="AGGREGATE",
            layer="DOMAIN",
        )
        result = engine.render(ctx)

        expected_path = "src/main/java/com/example/domain/order/OrderAggregate.java"
        assert result.file_path == expected_path

    def test_file_path_for_different_layers(self):
        """레이어별 파일 경로 생성"""
        engine = get_template_engine()

        test_cases = [
            ("OrderAggregate", "com.example.domain.order", "AGGREGATE", "DOMAIN"),
            (
                "CreateOrderUseCase",
                "com.example.application.order",
                "USE_CASE",
                "APPLICATION",
            ),
            ("OrderEntity", "com.example.persistence.order", "ENTITY", "PERSISTENCE"),
            ("OrderController", "com.example.api.order", "CONTROLLER", "REST_API"),
        ]

        for class_name, package, class_type, layer in test_cases:
            ctx = TemplateContext(
                class_name=class_name,
                package_name=package,
                class_type=class_type,
                layer=layer,
            )
            result = engine.render(ctx)

            # 파일 경로가 패키지 구조와 일치하는지 확인
            package_path = package.replace(".", "/")
            assert package_path in result.file_path
            assert f"{class_name}.java" in result.file_path
