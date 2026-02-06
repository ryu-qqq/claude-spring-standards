"""
Layer-Specific Skeleton Generator Tests

AESA-121~124: 레이어별 스켈레톤 생성기 검증 테스트

Note: FastMCP의 @mcp.tool() 데코레이터로 래핑된 함수들은 직접 호출할 수 없으므로,
TemplateEngine을 직접 사용하여 레이어별 템플릿 생성 로직을 검증합니다.
"""

import sys
import os
import re

# 프로젝트 루트 추가
sys.path.insert(
    0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
)

from src.template.engine import get_template_engine, BASE_TEMPLATES, CLASS_TYPE_TO_LAYER
from src.template.models import TemplateContext, FieldDefinition, MethodDefinition


class TestDomainLayerSkeletonGenerators:
    """AESA-121: Domain Layer 스켈레톤 생성기 테스트"""

    def test_generate_aggregate_skeleton_basic(self):
        """Aggregate 기본 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.order.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
        )
        result = engine.render(ctx)

        assert result.class_name == "Order"
        assert result.class_type == "AGGREGATE"
        assert result.layer == "DOMAIN"
        assert "public class Order" in result.code
        assert "package com.example.order.domain;" in result.code
        assert "Order.java" in result.file_path

    def test_generate_aggregate_skeleton_with_fields_and_methods(self):
        """Aggregate 필드 및 비즈니스 메서드 포함 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.order.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
            description="주문 애그리게이트",
            fields=[
                FieldDefinition(name="orderId", type="Long", description="주문 ID"),
                FieldDefinition(
                    name="status", type="OrderStatus", description="주문 상태"
                ),
            ],
            methods=[
                MethodDefinition(name="complete", description="주문 완료"),
                MethodDefinition(
                    name="cancel", return_type="void", description="주문 취소"
                ),
            ],
        )
        result = engine.render(ctx)

        assert "orderId" in result.code
        assert "status" in result.code
        assert "complete" in result.code
        assert "cancel" in result.code
        # Lombok 금지 규칙 적용 확인
        assert "@Data" not in result.code
        assert "@Getter" not in result.code
        assert "@Setter" not in result.code

    def test_generate_value_object_skeleton_basic(self):
        """Value Object 기본 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Money",
            package_name="com.example.order.domain",
            class_type="VALUE_OBJECT",
            layer="DOMAIN",
        )
        result = engine.render(ctx)

        assert result.class_name == "Money"
        assert result.class_type == "VALUE_OBJECT"
        assert result.layer == "DOMAIN"
        assert "public final class Money" in result.code
        assert "Money.java" in result.file_path

    def test_generate_value_object_skeleton_with_fields(self):
        """Value Object 필드 포함 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Money",
            package_name="com.example.order.domain",
            class_type="VALUE_OBJECT",
            layer="DOMAIN",
            fields=[
                FieldDefinition(name="amount", type="BigDecimal", is_final=True),
                FieldDefinition(name="currency", type="String", is_final=True),
            ],
        )
        result = engine.render(ctx)

        assert "amount" in result.code
        assert "currency" in result.code

    def test_generate_domain_event_skeleton(self):
        """Domain Event 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderCreatedEvent",
            package_name="com.example.order.domain.event",
            class_type="DOMAIN_EVENT",
            layer="DOMAIN",
            fields=[
                FieldDefinition(name="orderId", type="Long"),
                FieldDefinition(name="customerId", type="Long"),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderCreatedEvent"
        assert result.class_type == "DOMAIN_EVENT"
        assert result.layer == "DOMAIN"
        assert "public record OrderCreatedEvent" in result.code
        assert "orderId" in result.code

    def test_generate_domain_exception_skeleton(self):
        """Domain Exception 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderNotFoundException",
            package_name="com.example.order.domain.exception",
            class_type="DOMAIN_EXCEPTION",
            layer="DOMAIN",
            description="주문을 찾을 수 없을 때 발생",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderNotFoundException"
        assert result.class_type == "DOMAIN_EXCEPTION"
        assert result.layer == "DOMAIN"
        assert "extends RuntimeException" in result.code


class TestApplicationLayerSkeletonGenerators:
    """AESA-122: Application Layer 스켈레톤 생성기 테스트"""

    def test_generate_use_case_skeleton_basic(self):
        """UseCase 기본 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderUseCase",
            package_name="com.example.order.application.usecase",
            class_type="USE_CASE",
            layer="APPLICATION",
        )
        result = engine.render(ctx)

        assert result.class_name == "CreateOrderUseCase"
        assert result.class_type == "USE_CASE"
        assert result.layer == "APPLICATION"
        assert "@Service" in result.code
        assert "CreateOrderUseCase.java" in result.file_path

    def test_generate_use_case_skeleton_with_dependencies(self):
        """UseCase 의존성 및 실행 메서드 포함 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderUseCase",
            package_name="com.example.order.application.usecase",
            class_type="USE_CASE",
            layer="APPLICATION",
            description="주문 생성 유스케이스",
            fields=[
                FieldDefinition(name="orderRepository", type="OrderRepository"),
                FieldDefinition(name="eventPublisher", type="DomainEventPublisher"),
            ],
            methods=[
                MethodDefinition(
                    name="execute",
                    parameters=[("CreateOrderCommand", "command")],
                    return_type="OrderId",
                )
            ],
        )
        result = engine.render(ctx)

        assert "orderRepository" in result.code
        assert "eventPublisher" in result.code
        assert "execute" in result.code

    def test_generate_port_in_skeleton(self):
        """Input Port 인터페이스 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderPort",
            package_name="com.example.order.application.port.in",
            class_type="PORT_IN",
            layer="APPLICATION",
            methods=[
                MethodDefinition(
                    name="createOrder",
                    parameters=[("CreateOrderCommand", "command")],
                    return_type="OrderId",
                )
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "CreateOrderPort"
        assert result.class_type == "PORT_IN"
        assert result.layer == "APPLICATION"
        assert "interface CreateOrderPort" in result.code

    def test_generate_port_out_skeleton(self):
        """Output Port 인터페이스 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="LoadOrderPort",
            package_name="com.example.order.application.port.out",
            class_type="PORT_OUT",
            layer="APPLICATION",
            methods=[
                MethodDefinition(
                    name="loadById",
                    parameters=[("Long", "orderId")],
                    return_type="Optional<Order>",
                )
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "LoadOrderPort"
        assert result.class_type == "PORT_OUT"
        assert result.layer == "APPLICATION"
        assert "interface LoadOrderPort" in result.code


class TestPersistenceLayerSkeletonGenerators:
    """AESA-123: Persistence Layer 스켈레톤 생성기 테스트"""

    def test_generate_entity_skeleton_basic(self):
        """Entity 기본 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderEntity",
            package_name="com.example.order.adapter.out.persistence",
            class_type="ENTITY",
            layer="ADAPTER_OUT",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderEntity"
        assert result.class_type == "ENTITY"
        assert result.layer == "ADAPTER_OUT"
        assert "@Entity" in result.code
        assert "@Table" in result.code
        assert "OrderEntity.java" in result.file_path

    def test_generate_entity_skeleton_with_fields(self):
        """Entity 필드 포함 생성 (Long FK 전략)"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderEntity",
            package_name="com.example.order.adapter.out.persistence",
            class_type="ENTITY",
            layer="ADAPTER_OUT",
            description="주문 JPA 엔티티",
            fields=[
                FieldDefinition(name="orderId", type="Long", description="주문 ID"),
                FieldDefinition(name="customerId", type="Long", description="고객 FK"),
                FieldDefinition(name="status", type="String", description="주문 상태"),
            ],
        )
        result = engine.render(ctx)

        assert "orderId" in result.code
        assert "customerId" in result.code
        # JPA 관계 어노테이션 금지 확인 (주석이 아닌 실제 코드에서)
        # 템플릿에는 주석으로 @OneToMany, @ManyToOne 언급이 있지만 실제 필드에는 사용하지 않음
        # 실제 어노테이션 사용 패턴: 줄 시작 후 공백 + @ + 어노테이션명 (주석 내부가 아닌 경우)
        annotation_pattern_one = re.compile(r"^\s*@OneToMany", re.MULTILINE)
        annotation_pattern_many = re.compile(r"^\s*@ManyToOne", re.MULTILINE)
        assert not annotation_pattern_one.search(result.code), (
            "Entity should not use @OneToMany"
        )
        assert not annotation_pattern_many.search(result.code), (
            "Entity should not use @ManyToOne"
        )

    def test_generate_repository_skeleton(self):
        """JPA Repository 인터페이스 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderJpaRepository",
            package_name="com.example.order.adapter.out.persistence",
            class_type="JPA_REPOSITORY",
            layer="ADAPTER_OUT",
            methods=[
                MethodDefinition(
                    name="findByCustomerId",
                    parameters=[("Long", "customerId")],
                    return_type="List<OrderEntity>",
                )
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderJpaRepository"
        assert result.class_type == "JPA_REPOSITORY"
        assert result.layer == "ADAPTER_OUT"
        assert "interface" in result.code
        assert "JpaRepository" in result.code

    def test_generate_persistence_adapter_skeleton(self):
        """Persistence Adapter 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderPersistenceAdapter",
            package_name="com.example.order.adapter.out.persistence",
            class_type="ADAPTER",
            layer="ADAPTER_OUT",
            interfaces=["LoadOrderPort", "SaveOrderPort"],
            fields=[
                FieldDefinition(name="orderRepository", type="OrderJpaRepository"),
                FieldDefinition(name="orderMapper", type="OrderMapper"),
            ],
            methods=[
                MethodDefinition(
                    name="loadById",
                    parameters=[("Long", "orderId")],
                    return_type="Optional<Order>",
                ),
                MethodDefinition(
                    name="save",
                    parameters=[("Order", "order")],
                    return_type="void",
                ),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderPersistenceAdapter"
        assert result.class_type == "ADAPTER"
        assert result.layer == "ADAPTER_OUT"
        assert "@Repository" in result.code
        assert "implements LoadOrderPort, SaveOrderPort" in result.code


class TestRestApiLayerSkeletonGenerators:
    """AESA-124: REST API Layer 스켈레톤 생성기 테스트"""

    def test_generate_controller_skeleton_basic(self):
        """Controller 기본 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderController",
            package_name="com.example.order.adapter.in.rest",
            class_type="CONTROLLER",
            layer="ADAPTER_IN",
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderController"
        assert result.class_type == "CONTROLLER"
        assert result.layer == "ADAPTER_IN"
        assert "@RestController" in result.code
        assert "@RequestMapping" in result.code
        # @Transactional 금지 확인 (주석이 아닌 실제 코드에서)
        # 템플릿에는 주석으로 @Transactional 언급이 있지만 실제 어노테이션으로는 사용하지 않음
        annotation_pattern = re.compile(r"^\s*@Transactional", re.MULTILINE)
        assert not annotation_pattern.search(result.code), (
            "Controller should not use @Transactional"
        )

    def test_generate_controller_skeleton_with_endpoints(self):
        """Controller 엔드포인트 포함 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderController",
            package_name="com.example.order.adapter.in.rest",
            class_type="CONTROLLER",
            layer="ADAPTER_IN",
            description="주문 REST API",
            fields=[
                FieldDefinition(name="createOrderUseCase", type="CreateOrderUseCase"),
            ],
            methods=[
                MethodDefinition(
                    name="createOrder",
                    annotations=["@PostMapping"],
                    parameters=[("CreateOrderRequest", "request")],
                    return_type="OrderResponse",
                ),
                MethodDefinition(
                    name="getOrder",
                    annotations=['@GetMapping("/{orderId}")'],
                    parameters=[("Long", "orderId")],
                    return_type="OrderResponse",
                ),
            ],
        )
        result = engine.render(ctx)

        assert "createOrder" in result.code
        assert "getOrder" in result.code

    def test_generate_request_dto_skeleton(self):
        """Request DTO (Record) 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderRequest",
            package_name="com.example.order.adapter.in.rest.dto",
            class_type="REQUEST_DTO",
            layer="ADAPTER_IN",
            fields=[
                FieldDefinition(name="customerId", type="Long"),
                FieldDefinition(name="productId", type="Long"),
                FieldDefinition(name="quantity", type="Integer"),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "CreateOrderRequest"
        assert result.class_type == "REQUEST_DTO"
        assert result.layer == "ADAPTER_IN"
        assert "public record CreateOrderRequest" in result.code
        assert "customerId" in result.code

    def test_generate_response_dto_skeleton(self):
        """Response DTO (Record) 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderResponse",
            package_name="com.example.order.adapter.in.rest.dto",
            class_type="RESPONSE_DTO",
            layer="ADAPTER_IN",
            fields=[
                FieldDefinition(name="orderId", type="Long"),
                FieldDefinition(name="status", type="String"),
                FieldDefinition(name="totalAmount", type="BigDecimal"),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderResponse"
        assert result.class_type == "RESPONSE_DTO"
        assert result.layer == "ADAPTER_IN"
        assert "public record OrderResponse" in result.code

    def test_generate_mapper_skeleton(self):
        """Mapper 클래스 생성"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderMapper",
            package_name="com.example.order.adapter.in.rest",
            class_type="MAPPER",
            layer="ADAPTER_IN",
            methods=[
                MethodDefinition(
                    name="toResponse",
                    parameters=[("Order", "order")],
                    return_type="OrderResponse",
                ),
                MethodDefinition(
                    name="toCommand",
                    parameters=[("CreateOrderRequest", "request")],
                    return_type="CreateOrderCommand",
                ),
            ],
        )
        result = engine.render(ctx)

        assert result.class_name == "OrderMapper"
        assert result.class_type == "MAPPER"
        assert result.layer == "ADAPTER_IN"
        assert "@Component" in result.code
        assert "toResponse" in result.code
        assert "toCommand" in result.code


class TestSkeletonGeneratorAppliedRules:
    """적용된 규칙 검증 테스트"""

    def test_aggregate_applied_rules(self):
        """Aggregate 적용 규칙 확인"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
        )
        result = engine.render(ctx)

        assert len(result.applied_rules) > 0

    def test_entity_applied_rules(self):
        """Entity 적용 규칙 확인"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderEntity",
            package_name="com.example.persistence",
            class_type="ENTITY",
            layer="ADAPTER_OUT",
        )
        result = engine.render(ctx)

        assert len(result.applied_rules) > 0

    def test_controller_applied_rules(self):
        """Controller 적용 규칙 확인"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderController",
            package_name="com.example.api",
            class_type="CONTROLLER",
            layer="ADAPTER_IN",
        )
        result = engine.render(ctx)

        assert len(result.applied_rules) > 0


class TestFilePathGeneration:
    """파일 경로 생성 테스트"""

    def test_domain_layer_file_path(self):
        """Domain 레이어 파일 경로"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.order.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
        )
        result = engine.render(ctx)

        assert "src/main/java/com/example/order/domain/Order.java" == result.file_path

    def test_application_layer_file_path(self):
        """Application 레이어 파일 경로"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="CreateOrderUseCase",
            package_name="com.example.order.application.usecase",
            class_type="USE_CASE",
            layer="APPLICATION",
        )
        result = engine.render(ctx)

        assert (
            "src/main/java/com/example/order/application/usecase/CreateOrderUseCase.java"
            == result.file_path
        )

    def test_persistence_layer_file_path(self):
        """Persistence 레이어 파일 경로"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderEntity",
            package_name="com.example.order.adapter.out.persistence",
            class_type="ENTITY",
            layer="ADAPTER_OUT",
        )
        result = engine.render(ctx)

        assert (
            "src/main/java/com/example/order/adapter/out/persistence/OrderEntity.java"
            == result.file_path
        )

    def test_rest_api_layer_file_path(self):
        """REST API 레이어 파일 경로"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderController",
            package_name="com.example.order.adapter.in.rest",
            class_type="CONTROLLER",
            layer="ADAPTER_IN",
        )
        result = engine.render(ctx)

        assert (
            "src/main/java/com/example/order/adapter/in/rest/OrderController.java"
            == result.file_path
        )


class TestImportsHandling:
    """Import 처리 테스트"""

    def test_custom_imports(self):
        """커스텀 import 추가"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.order.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
            imports=[
                "java.time.LocalDateTime",
                "java.util.UUID",
                "com.example.common.Money",
            ],
        )
        result = engine.render(ctx)

        assert "import java.time.LocalDateTime;" in result.code
        assert "import java.util.UUID;" in result.code
        assert "import com.example.common.Money;" in result.code


class TestInterfaceImplementation:
    """인터페이스 구현 테스트"""

    def test_aggregate_with_interfaces(self):
        """Aggregate 인터페이스 구현"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="Order",
            package_name="com.example.order.domain",
            class_type="AGGREGATE",
            layer="DOMAIN",
            interfaces=["Auditable", "Identifiable"],
        )
        result = engine.render(ctx)

        assert "implements Auditable, Identifiable" in result.code

    def test_adapter_with_multiple_ports(self):
        """Adapter 복수 포트 구현"""
        engine = get_template_engine()
        ctx = TemplateContext(
            class_name="OrderPersistenceAdapter",
            package_name="com.example.order.adapter.out.persistence",
            class_type="ADAPTER",
            layer="ADAPTER_OUT",
            interfaces=["LoadOrderPort", "SaveOrderPort", "DeleteOrderPort"],
        )
        result = engine.render(ctx)

        assert "implements LoadOrderPort, SaveOrderPort, DeleteOrderPort" in result.code


class TestClassTypeToLayerMapping:
    """CLASS_TYPE_TO_LAYER 매핑 검증"""

    def test_domain_types_mapped_correctly(self):
        """Domain 타입 매핑 확인"""
        domain_types = ["AGGREGATE", "VALUE_OBJECT", "DOMAIN_EVENT", "DOMAIN_EXCEPTION"]
        for class_type in domain_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "DOMAIN", (
                f"{class_type} should map to DOMAIN"
            )

    def test_application_types_mapped_correctly(self):
        """Application 타입 매핑 확인"""
        application_types = ["USE_CASE", "PORT_IN", "PORT_OUT"]
        for class_type in application_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "APPLICATION", (
                f"{class_type} should map to APPLICATION"
            )

    def test_persistence_types_mapped_correctly(self):
        """Persistence 타입 매핑 확인"""
        persistence_types = ["ENTITY", "JPA_REPOSITORY", "ADAPTER"]
        for class_type in persistence_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "ADAPTER_OUT", (
                f"{class_type} should map to ADAPTER_OUT"
            )

    def test_rest_api_types_mapped_correctly(self):
        """REST API 타입 매핑 확인"""
        rest_api_types = ["CONTROLLER", "REQUEST_DTO", "RESPONSE_DTO", "MAPPER"]
        for class_type in rest_api_types:
            assert CLASS_TYPE_TO_LAYER[class_type] == "ADAPTER_IN", (
                f"{class_type} should map to ADAPTER_IN"
            )


class TestAllClassTypesHaveTemplates:
    """모든 클래스 타입 템플릿 존재 확인"""

    def test_all_14_class_types_have_templates(self):
        """14개 클래스 타입 모두 템플릿 존재"""
        expected_types = [
            # Domain (4)
            "AGGREGATE",
            "VALUE_OBJECT",
            "DOMAIN_EVENT",
            "DOMAIN_EXCEPTION",
            # Application (3)
            "USE_CASE",
            "PORT_IN",
            "PORT_OUT",
            # Persistence (3)
            "ENTITY",
            "JPA_REPOSITORY",
            "ADAPTER",
            # REST API (4)
            "CONTROLLER",
            "REQUEST_DTO",
            "RESPONSE_DTO",
            "MAPPER",
        ]

        assert len(expected_types) == 14

        for class_type in expected_types:
            assert class_type in BASE_TEMPLATES, f"Missing template for {class_type}"
            assert len(BASE_TEMPLATES[class_type]) > 0, (
                f"Empty template for {class_type}"
            )
