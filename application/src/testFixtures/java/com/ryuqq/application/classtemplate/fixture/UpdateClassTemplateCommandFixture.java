package com.ryuqq.application.classtemplate.fixture;

import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import java.util.List;

/**
 * UpdateClassTemplateCommand Test Fixture
 *
 * @author development-team
 */
public final class UpdateClassTemplateCommandFixture {

    private UpdateClassTemplateCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateClassTemplateCommand defaultCommand() {
        return new UpdateClassTemplateCommand(
                1L,
                1L, // classTypeId (AGGREGATE)
                "AGG-001",
                "^[A-Z]\\w+$",
                "업데이트된 Aggregate 템플릿",
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of());
    }
}
