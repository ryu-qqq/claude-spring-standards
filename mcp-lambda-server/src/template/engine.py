"""
Jinja2 Template Engine

Spring Boot 헥사고날 아키텍처에 맞는 Java 코드 스켈레톤 생성
"""

import re
from typing import Optional

from jinja2 import Environment, BaseLoader, TemplateNotFound

from .models import (
    TemplateContext,
    GeneratedCode,
)


# ============================================
# Layer → Class Type 매핑
# ============================================

LAYER_CLASS_TYPES = {
    "DOMAIN": [
        "AGGREGATE",
        "VALUE_OBJECT",
        "DOMAIN_EVENT",
        "DOMAIN_EXCEPTION",
    ],
    "APPLICATION": [
        "USE_CASE",
        "COMMAND_SERVICE",
        "QUERY_SERVICE",
        "PORT_IN",
        "PORT_OUT",
    ],
    "ADAPTER_OUT": [
        "ENTITY",
        "JPA_REPOSITORY",
        "ADAPTER",
    ],
    "ADAPTER_IN": [
        "CONTROLLER",
        "REQUEST_DTO",
        "RESPONSE_DTO",
        "MAPPER",
        "ASSEMBLER",
    ],
}

CLASS_TYPE_TO_LAYER = {
    class_type: layer
    for layer, class_types in LAYER_CLASS_TYPES.items()
    for class_type in class_types
}


# ============================================
# 기본 Jinja2 템플릿
# ============================================

BASE_TEMPLATES = {
    # Domain Layer
    "AGGREGATE": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}

/**
 * {{ description or class_name + " Aggregate Root" }}
{% if author %} * @author {{ author }}{% endif %}
 */
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }}{% if extends %} extends {{ extends }}{% endif %}{% if interfaces %} implements {{ interfaces | join(", ") }}{% endif %} {

{% for field in fields %}
    {% if field.description %}
    /** {{ field.description }} */
    {% endif %}
    {{ field.access_modifier }}{% if field.is_final %} final{% endif %} {{ field.type }} {{ field.name }}{% if field.default_value %} = {{ field.default_value }}{% endif %};

{% endfor %}
    // ============================================
    // Factory Method (Aggregate Creation)
    // ============================================

    public static {{ class_name }} create({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ class_name }} aggregate = new {{ class_name }}();
{% for field in fields %}
        aggregate.{{ field.name }} = {{ field.name }};
{% endfor %}
        return aggregate;
    }

    // Protected constructor for JPA
    protected {{ class_name }}() {
    }

    // ============================================
    // Getters (No Lombok, No Setter)
    // ============================================

{% for field in fields %}
    public {{ field.type }} get{{ field.name | capitalize_first }}() {
        return this.{{ field.name }};
    }

{% endfor %}
{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {% for annotation in method.annotations %}
    {{ annotation }}
    {% endfor %}
    {{ method.access_modifier }}{% if method.is_static %} static{% endif %} {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ method.body or "// TODO: Implement business logic" }}
    }

{% endfor %}
}
""",
    "VALUE_OBJECT": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}

/**
 * {{ description or class_name + " Value Object" }}
 * Immutable value object following DDD principles.
{% if author %} * @author {{ author }}{% endif %}
 */
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public final class {{ class_name }}{% if interfaces %} implements {{ interfaces | join(", ") }}{% endif %} {

{% for field in fields %}
    {% if field.description %}
    /** {{ field.description }} */
    {% endif %}
    private final {{ field.type }} {{ field.name }};

{% endfor %}
    // ============================================
    // Private Constructor (Use factory method)
    // ============================================

    private {{ class_name }}({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
{% for field in fields %}
        this.{{ field.name }} = {{ field.name }};
{% endfor %}
    }

    // ============================================
    // Factory Method
    // ============================================

    public static {{ class_name }} of({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        // Validation
{% for field in fields %}
{% if field.type == "String" %}
        if ({{ field.name }} == null || {{ field.name }}.isBlank()) {
            throw new IllegalArgumentException("{{ field.name }} cannot be null or blank");
        }
{% else %}
        if ({{ field.name }} == null) {
            throw new IllegalArgumentException("{{ field.name }} cannot be null");
        }
{% endif %}
{% endfor %}
        return new {{ class_name }}({% for field in fields %}{{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %});
    }

    // ============================================
    // Getters
    // ============================================

{% for field in fields %}
    public {{ field.type }} get{{ field.name | capitalize_first }}() {
        return this.{{ field.name }};
    }

{% endfor %}
    // ============================================
    // Value Object Equality
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        {{ class_name }} that = ({{ class_name }}) o;
        return {% for field in fields %}java.util.Objects.equals({{ field.name }}, that.{{ field.name }}){% if not loop.last %} &&
               {% endif %}{% endfor %};
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash({% for field in fields %}{{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %});
    }

    @Override
    public String toString() {
        return "{{ class_name }}{" +
{% for field in fields %}
                "{{ field.name }}=" + {{ field.name }}{% if not loop.last %} +
                ", " +{% endif %}

{% endfor %}
                '}';
    }
}
""",
    "DOMAIN_EVENT": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import java.time.Instant;
import java.util.UUID;

/**
 * {{ description or class_name + " Domain Event" }}
{% if author %} * @author {{ author }}{% endif %}
 */
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public record {{ class_name }}(
    String eventId,
    Instant occurredAt{% for field in fields %},
    {{ field.type }} {{ field.name }}{% endfor %}

) {

    // ============================================
    // Factory Method
    // ============================================

    public static {{ class_name }} create({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        return new {{ class_name }}(
            UUID.randomUUID().toString(),
            Instant.now(){% for field in fields %},
            {{ field.name }}{% endfor %}

        );
    }
}
""",
    "DOMAIN_EXCEPTION": """package {{ package_name }};

/**
 * {{ description or class_name + " Domain Exception" }}
{% if author %} * @author {{ author }}{% endif %}
 */
public class {{ class_name }} extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "{{ extra.get('default_message', class_name + ' occurred') }}";

    public {{ class_name }}() {
        super(DEFAULT_MESSAGE);
    }

    public {{ class_name }}(String message) {
        super(message);
    }

    public {{ class_name }}(String message, Throwable cause) {
        super(message, cause);
    }

    public {{ class_name }}(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
""",
    # Application Layer
    "USE_CASE": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {{ description or class_name + " Use Case" }}
 * Single Responsibility: One use case per class
{% if author %} * @author {{ author }}{% endif %}
 */
@Service
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }}{% if interfaces %} implements {{ interfaces | join(", ") }}{% endif %} {

{% for field in fields %}
    private final {{ field.type }} {{ field.name }};
{% endfor %}

    public {{ class_name }}({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
{% for field in fields %}
        this.{{ field.name }} = {{ field.name }};
{% endfor %}
    }

    // ============================================
    // Use Case Execution
    // ============================================

    @Transactional
{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {% for annotation in method.annotations %}
    {{ annotation }}
    {% endfor %}
    {{ method.access_modifier }} {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ method.body or "// TODO: Implement use case logic" }}
    }

{% endfor %}
}
""",
    "PORT_IN": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}

/**
 * {{ description or class_name + " Input Port" }}
 * Defines the contract for incoming requests to the application layer.
{% if author %} * @author {{ author }}{% endif %}
 */
public interface {{ class_name }} {

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %});

{% endfor %}
}
""",
    "PORT_OUT": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}

/**
 * {{ description or class_name + " Output Port" }}
 * Defines the contract for outgoing operations from the application layer.
{% if author %} * @author {{ author }}{% endif %}
 */
public interface {{ class_name }} {

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %});

{% endfor %}
}
""",
    # Persistence Layer
    "ENTITY": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * {{ description or class_name + " JPA Entity" }}
 * Note: No JPA relationship annotations (@OneToMany, @ManyToOne, etc.) - Use Long FK strategy
{% if author %} * @author {{ author }}{% endif %}
 */
@Entity
@Table(name = "{{ extra.get('table_name', class_name | to_snake_case) }}")
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }}{% if extends %} extends {{ extends }}{% endif %} {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

{% for field in fields %}
    {% if field.description %}
    /** {{ field.description }} */
    {% endif %}
    @Column(name = "{{ field.name | to_snake_case }}"{% if field.type == "String" %}, length = 255{% endif %})
    private {{ field.type }} {{ field.name }};

{% endfor %}
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Protected constructor for JPA
    protected {{ class_name }}() {
    }

    // ============================================
    // Lifecycle Callbacks
    // ============================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ============================================
    // Getters (No Lombok)
    // ============================================

    public Long getId() {
        return this.id;
    }

{% for field in fields %}
    public {{ field.type }} get{{ field.name | capitalize_first }}() {
        return this.{{ field.name }};
    }

{% endfor %}
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
""",
    "JPA_REPOSITORY": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * {{ description or class_name + " JPA Repository" }}
{% if author %} * @author {{ author }}{% endif %}
 */
public interface {{ class_name }} extends JpaRepository<{{ extra.get('entity_type', 'Entity') }}, Long> {

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %});

{% endfor %}
}
""",
    "ADAPTER": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {{ description or class_name + " Persistence Adapter" }}
 * Implements output port for persistence operations.
{% if author %} * @author {{ author }}{% endif %}
 */
@Repository
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }}{% if interfaces %} implements {{ interfaces | join(", ") }}{% endif %} {

{% for field in fields %}
    private final {{ field.type }} {{ field.name }};
{% endfor %}

    public {{ class_name }}({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
{% for field in fields %}
        this.{{ field.name }} = {{ field.name }};
{% endfor %}
    }

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    @Override
    {% for annotation in method.annotations %}
    {{ annotation }}
    {% endfor %}
    @Transactional(readOnly = {{ 'true' if 'find' in method.name.lower() or 'get' in method.name.lower() else 'false' }})
    {{ method.access_modifier }} {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ method.body or "// TODO: Implement persistence logic" }}
    }

{% endfor %}
}
""",
    # REST API Layer
    "CONTROLLER": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * {{ description or class_name + " REST Controller" }}
 * Note: No @Transactional on Controller - Transaction management is in UseCase
{% if author %} * @author {{ author }}{% endif %}
 */
@RestController
@RequestMapping("{{ extra.get('base_path', '/api/v1/' + (class_name | to_kebab_case | replace('-controller', ''))) }}")
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }} {

{% for field in fields %}
    private final {{ field.type }} {{ field.name }};
{% endfor %}

    public {{ class_name }}({% for field in fields %}{{ field.type }} {{ field.name }}{% if not loop.last %}, {% endif %}{% endfor %}) {
{% for field in fields %}
        this.{{ field.name }} = {{ field.name }};
{% endfor %}
    }

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {% for annotation in method.annotations %}
    {{ annotation }}
    {% endfor %}
    {{ method.access_modifier }} ResponseEntity<{{ method.return_type }}> {{ method.name }}({% for param in method.parameters %}{% if '@RequestBody' in param[0] or '@Valid' in param[0] %}{{ param[0] }} {{ param[1] }}{% else %}{{ param[0] }} {{ param[1] }}{% endif %}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ method.body or "// TODO: Implement endpoint logic" }}
    }

{% endfor %}
}
""",
    "REQUEST_DTO": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import jakarta.validation.constraints.*;

/**
 * {{ description or class_name + " Request DTO" }}
 * Using Java Record for immutability.
{% if author %} * @author {{ author }}{% endif %}
 */
public record {{ class_name }}(
{% for field in fields %}
    {% if field.description %}
    /** {{ field.description }} */
    {% endif %}
    {% if field.type == "String" and not field.default_value %}
    @NotBlank(message = "{{ field.name }} is required")
    {% elif field.type != "String" and not field.default_value %}
    @NotNull(message = "{{ field.name }} is required")
    {% endif %}
    {{ field.type }} {{ field.name }}{% if not loop.last %},{% endif %}

{% endfor %}
) {
}
""",
    "RESPONSE_DTO": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}

/**
 * {{ description or class_name + " Response DTO" }}
 * Using Java Record for immutability.
{% if author %} * @author {{ author }}{% endif %}
 */
public record {{ class_name }}(
{% for field in fields %}
    {% if field.description %}
    /** {{ field.description }} */
    {% endif %}
    {{ field.type }} {{ field.name }}{% if not loop.last %},{% endif %}

{% endfor %}
) {

    // ============================================
    // Factory Methods
    // ============================================

    public static {{ class_name }} from({{ extra.get('source_type', 'Object') }} source) {
        return new {{ class_name }}(
{% for field in fields %}
            source.get{{ field.name | capitalize_first }}(){% if not loop.last %},{% endif %}

{% endfor %}
        );
    }
}
""",
    "MAPPER": """package {{ package_name }};

{% for imp in imports %}
import {{ imp }};
{% endfor %}
import org.springframework.stereotype.Component;

/**
 * {{ description or class_name + " Mapper" }}
 * Maps between domain objects and DTOs.
{% if author %} * @author {{ author }}{% endif %}
 */
@Component
{% for annotation in annotations %}
{{ annotation }}
{% endfor %}
public class {{ class_name }} {

{% for method in methods %}
    {% if method.description %}
    /**
     * {{ method.description }}
     */
    {% endif %}
    {{ method.access_modifier }} {{ method.return_type }} {{ method.name }}({% for param in method.parameters %}{{ param[0] }} {{ param[1] }}{% if not loop.last %}, {% endif %}{% endfor %}) {
        {{ method.body or "// TODO: Implement mapping logic" }}
    }

{% endfor %}
}
""",
}


# ============================================
# Custom Jinja2 Filters
# ============================================


def capitalize_first(s: str) -> str:
    """첫 글자만 대문자로"""
    if not s:
        return s
    return s[0].upper() + s[1:]


def to_snake_case(s: str) -> str:
    """camelCase/PascalCase를 snake_case로 변환"""
    # 대문자 앞에 _ 추가
    result = re.sub(r"([A-Z])", r"_\1", s)
    # 맨 앞 _ 제거 및 소문자 변환
    return result.lstrip("_").lower()


def to_kebab_case(s: str) -> str:
    """camelCase/PascalCase를 kebab-case로 변환"""
    return to_snake_case(s).replace("_", "-")


def to_camel_case(s: str) -> str:
    """snake_case를 camelCase로 변환"""
    components = s.split("_")
    return components[0] + "".join(x.title() for x in components[1:])


def to_pascal_case(s: str) -> str:
    """snake_case를 PascalCase로 변환"""
    return "".join(x.title() for x in s.split("_"))


# ============================================
# Dynamic Template Loader
# ============================================


class DynamicTemplateLoader(BaseLoader):
    """동적으로 템플릿을 로드하는 Jinja2 Loader"""

    def __init__(self, templates: dict[str, str]):
        self.templates = templates

    def get_source(
        self, environment: Environment, template: str
    ) -> tuple[str, str, callable]:
        if template in self.templates:
            source = self.templates[template]
            return source, template, lambda: True
        raise TemplateNotFound(template)


# ============================================
# Template Engine
# ============================================


class TemplateEngine:
    """Jinja2 기반 Java 코드 생성 엔진"""

    def __init__(self, custom_templates: Optional[dict[str, str]] = None):
        """
        템플릿 엔진 초기화

        Args:
            custom_templates: 사용자 정의 템플릿 (class_type -> template_code)
        """
        # 기본 템플릿과 사용자 정의 템플릿 병합
        self.templates = {**BASE_TEMPLATES}
        if custom_templates:
            self.templates.update(custom_templates)

        # Jinja2 환경 설정
        self.env = Environment(
            loader=DynamicTemplateLoader(self.templates),
            trim_blocks=True,
            lstrip_blocks=True,
            keep_trailing_newline=True,
        )

        # 커스텀 필터 등록
        self.env.filters["capitalize_first"] = capitalize_first
        self.env.filters["to_snake_case"] = to_snake_case
        self.env.filters["to_kebab_case"] = to_kebab_case
        self.env.filters["to_camel_case"] = to_camel_case
        self.env.filters["to_pascal_case"] = to_pascal_case

    def add_template(self, class_type: str, template_code: str) -> None:
        """템플릿 추가/업데이트"""
        self.templates[class_type] = template_code
        # Loader 업데이트
        self.env.loader = DynamicTemplateLoader(self.templates)

    def get_available_templates(self) -> list[str]:
        """사용 가능한 템플릿 목록 반환"""
        return list(self.templates.keys())

    def has_template(self, class_type: str) -> bool:
        """템플릿 존재 여부 확인"""
        return class_type in self.templates

    def render(self, context: TemplateContext) -> GeneratedCode:
        """
        템플릿 렌더링

        Args:
            context: 템플릿 컨텍스트

        Returns:
            생성된 코드 결과

        Raises:
            ValueError: 템플릿이 없는 경우
        """
        if not self.has_template(context.class_type):
            raise ValueError(f"No template found for class type: {context.class_type}")

        # 템플릿 로드 및 렌더링
        template = self.env.get_template(context.class_type)
        code = template.render(**context.model_dump())

        # 파일 경로 생성
        file_path = self._generate_file_path(context)

        # 적용된 규칙 수집
        applied_rules = self._collect_applied_rules(context.class_type, context.layer)

        return GeneratedCode(
            class_name=context.class_name,
            class_type=context.class_type,
            layer=context.layer,
            package_name=context.package_name,
            file_path=file_path,
            code=code,
            applied_rules=applied_rules,
            warnings=[],
        )

    def _generate_file_path(self, context: TemplateContext) -> str:
        """파일 경로 생성"""
        # 패키지를 디렉토리 구조로 변환
        package_path = context.package_name.replace(".", "/")
        return f"src/main/java/{package_path}/{context.class_name}.java"

    def _collect_applied_rules(self, class_type: str, layer: str) -> list[str]:
        """적용된 규칙 코드 목록 수집"""
        # 기본적으로 적용되는 규칙들
        rules = []

        # 레이어별 기본 규칙
        layer_rules = {
            "DOMAIN": ["AGG-001", "AGG-014"],  # Lombok 금지, Law of Demeter
            "APPLICATION": ["APP-001", "APP-002"],  # CQRS, Transaction 관리
            "ADAPTER_OUT": ["ENT-002", "ENT-003"],  # Long FK, No JPA Relations
            "ADAPTER_IN": ["CTR-001", "CTR-005"],  # @Valid 필수, Transaction 금지
        }

        if layer in layer_rules:
            rules.extend(layer_rules[layer])

        return rules


# ============================================
# Singleton Instance
# ============================================

_engine: Optional[TemplateEngine] = None


def get_template_engine() -> TemplateEngine:
    """템플릿 엔진 싱글톤 반환"""
    global _engine
    if _engine is None:
        _engine = TemplateEngine()
    return _engine
