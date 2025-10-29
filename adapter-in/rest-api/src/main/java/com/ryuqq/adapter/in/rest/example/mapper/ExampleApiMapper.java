package com.ryuqq.adapter.in.rest.example.mapper;

import com.ryuqq.adapter.in.rest.example.dto.request.ExampleApiRequest;
import com.ryuqq.adapter.in.rest.example.dto.request.ExampleSearchApiRequest;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleDetailApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExamplePageApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleSliceApiResponse;
import com.ryuqq.application.common.dto.response.PageResponse;
import com.ryuqq.application.common.dto.response.SliceResponse;
import com.ryuqq.application.example.dto.command.CreateExampleCommand;
import com.ryuqq.application.example.dto.query.GetExampleQuery;
import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;
import com.ryuqq.application.example.dto.response.ExampleResponse;

import org.springframework.stereotype.Component;

/**
 * ExampleMapper - Example REST API ↔ Application Layer 변환
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.</p>
 *
 * <p><strong>변환 방향:</strong></p>
 * <ul>
 *   <li>Request → Command/Query (Controller → Application)</li>
 *   <li>Application Response → REST API Response (Application → Controller)</li>
 * </ul>
 *
 * <p><strong>CQRS 패턴 적용:</strong></p>
 * <ul>
 *   <li>Command: Create/Update 요청 변환</li>
 *   <li>Query: Get/Search 요청 변환</li>
 * </ul>
 *
 * <p><strong>의존성 역전 원칙 준수:</strong></p>
 * <ul>
 *   <li>Application Layer Response를 REST API Layer Response로 변환</li>
 *   <li>REST API Layer가 Application Layer에 의존하는 것은 OK</li>
 *   <li>Application Layer가 REST API Layer에 의존하는 것은 NG</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleApiMapper {

    /**
     * ExampleApiRequest → CreateExampleCommand 변환
     *
     * @param request REST API 생성 요청
     * @return Application Layer 생성 명령
     */
    public CreateExampleCommand toCreateCommand(ExampleApiRequest request) {
        return CreateExampleCommand.of(request.message());
    }

    /**
     * ID → GetExampleQuery 변환
     *
     * @param id Example ID
     * @return Application Layer 조회 쿼리
     */
    public GetExampleQuery toGetQuery(Long id) {
        return GetExampleQuery.of(id);
    }

    /**
     * ExampleSearchRequest → SearchExampleQuery 변환
     *
     * <p>REST API Layer의 검색 요청을 Application Layer의 쿼리로 변환합니다.</p>
     * <p>페이지네이션 타입은 cursor 유무로 자동 판단합니다.</p>
     *
     * @param request REST API 검색 요청
     * @return Application Layer 검색 쿼리
     */
    public SearchExampleQuery toSearchQuery(ExampleSearchApiRequest request) {
        // Cursor가 있으면 CURSOR 타입, 없으면 OFFSET 타입
        boolean isCursor = request.cursor() != null && !request.cursor().isBlank();

        if (isCursor) {
            return SearchExampleQuery.ofCursor(
                request.message(),
                request.status(),
                request.startDate(),
                request.endDate(),
                request.cursor(),
                request.size(),
                request.sortBy(),
                request.sortDirection()
            );
        } else {
            return SearchExampleQuery.ofOffset(
                request.message(),
                request.status(),
                request.startDate(),
                request.endDate(),
                request.page(),
                request.size(),
                request.sortBy(),
                request.sortDirection()
            );
        }
    }

    /**
     * ExampleResponse → ExampleApiResponse 변환
     *
     * <p>Application Layer의 응답을 REST API Layer의 응답으로 변환합니다.</p>
     *
     * @param response Application Layer 응답
     * @return REST API 응답
     */
    public ExampleApiResponse toApiResponse(ExampleResponse response) {
        return ExampleApiResponse.fromResponse(response.message());
    }

    /**
     * ExampleDetailResponse → ExampleDetailApiResponse 변환
     *
     * <p>Application Layer의 상세 응답을 REST API Layer의 상세 응답으로 변환합니다.</p>
     *
     * @param appResponse Application Layer 상세 응답
     * @return REST API 상세 응답
     */
    public ExampleDetailApiResponse toDetailApiResponse(ExampleDetailResponse appResponse) {
        return ExampleDetailApiResponse.from(appResponse);
    }

    /**
     * PageResponse → ExamplePageApiResponse 변환
     *
     * <p>Application Layer의 페이지 응답을 REST API Layer의 페이지 응답으로 변환합니다.</p>
     *
     * @param appPageResponse Application Layer 페이지 응답
     * @return REST API 페이지 응답
     */
    public ExamplePageApiResponse toPageApiResponse(PageResponse<ExampleDetailResponse> appPageResponse) {
        return ExamplePageApiResponse.from(appPageResponse);
    }

    /**
     * SliceResponse → ExampleSliceApiResponse 변환
     *
     * <p>Application Layer의 슬라이스 응답을 REST API Layer의 슬라이스 응답으로 변환합니다.</p>
     *
     * @param appSliceResponse Application Layer 슬라이스 응답
     * @return REST API 슬라이스 응답
     */
    public ExampleSliceApiResponse toSliceApiResponse(SliceResponse<ExampleDetailResponse> appSliceResponse) {
        return ExampleSliceApiResponse.from(appSliceResponse);
    }
}
