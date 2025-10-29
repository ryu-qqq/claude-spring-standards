package com.ryuqq.adapter.in.rest.example.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.DomainException;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ExampleErrorMapping implements ErrorMapper {

    private static final String PREFIX = "EXAMPLE_";
    private static final String TYPE_BASE = "https://api.example.com/problems/"; // 사내 문서 URL 권장

    private final MessageSource messageSource;

    public ExampleErrorMapping(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(String code) {
        return code != null && code.startsWith(PREFIX);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String code = ex.code();

        // 1) 상태코드 매핑 (컨텍스트별 정책)
        HttpStatus status = switch (code) {
            case "EXAMPLE_NOT_FOUND"     -> HttpStatus.NOT_FOUND;
            case "EXAMPLE_DUPLICATE_KEY" -> HttpStatus.CONFLICT;
            case "EXAMPLE_INVALID_STATE" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };

        // 2) 사용자 친화 타이틀/디테일 (i18n → 없으면 도메인 메시지)
        String titleKey  = "problem.title."  + code.toLowerCase(); // 예: problem.title.example_not_found
        String detailKey = "problem.detail." + code.toLowerCase(); // 예: problem.detail.example_not_found

        Object[] args = new Object[0]; // TODO: DomainException에 attrs() 메서드 추가 시 활성화
        String defaultTitle = status.getReasonPhrase();

        String title  = messageSource.getMessage(titleKey,  args, defaultTitle, locale);
        String detail = messageSource.getMessage(detailKey, args,
            ex.getMessage() != null ? ex.getMessage() : defaultTitle, locale);

        // 3) type URI (문서화 가능)

        URI type = URI.create(TYPE_BASE + code.toLowerCase().replace('_', '-'));

        return new MappedError(status, title, detail, type);
    }

    /** 간단 변환: attrs(Map)를 순서 없는 가변 인자로 변환 (실무에선 키순 정렬/명명 자리표시자 전략 권장) */
    private Object[] toArgs(Map<String, Object> attrs) {
        return attrs == null || attrs.isEmpty() ? new Object[0] : attrs.values().toArray();
    }

    //messages_ko.properties 예시(예: adapter-in/rest/example/src/main/resources):
    // problem.title.example_not_found=리소스를 찾을 수 없습니다
    // problem.detail.example_not_found=요청하신 예시 리소스를 찾을 수 없습니다. (id={0})
    //
    // problem.title.example_duplicate_key=중복된 데이터
    // problem.detail.example_duplicate_key=이미 존재하는 값입니다. (key={0})
    //
    // problem.title.example_invalid_state=요청을 처리할 수 없는 상태
    // problem.detail.example_invalid_state=현재 상태에서는 수행할 수 없습니다. (state={0})
}