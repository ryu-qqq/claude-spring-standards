package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.SearchClassTemplatesCursorApiRequest;
import java.util.List;

/**
 * SearchClassTemplatesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchClassTemplatesCursorApiRequestFixture {

    private SearchClassTemplatesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchClassTemplatesCursorApiRequest valid() {
        return new SearchClassTemplatesCursorApiRequest(null, 20, null, null);
    }

    public static SearchClassTemplatesCursorApiRequest validWithCursor() {
        return new SearchClassTemplatesCursorApiRequest("100", 20, null, null);
    }

    public static SearchClassTemplatesCursorApiRequest validWithStructureIds() {
        return new SearchClassTemplatesCursorApiRequest(null, 20, List.of(1L, 2L), null);
    }

    public static SearchClassTemplatesCursorApiRequest validWithClassTypeIds() {
        return new SearchClassTemplatesCursorApiRequest(null, 20, null, List.of(1L, 2L));
    }

    /**
     * @deprecated Use {@link #validWithClassTypeIds()} instead
     */
    @Deprecated
    public static SearchClassTemplatesCursorApiRequest validWithClassTypes() {
        return validWithClassTypeIds();
    }

    public static SearchClassTemplatesCursorApiRequest validWithFilters() {
        return new SearchClassTemplatesCursorApiRequest(null, 20, List.of(1L), List.of(1L));
    }

    public static SearchClassTemplatesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchClassTemplatesCursorApiRequest(null, 101, null, null);
    }
}
