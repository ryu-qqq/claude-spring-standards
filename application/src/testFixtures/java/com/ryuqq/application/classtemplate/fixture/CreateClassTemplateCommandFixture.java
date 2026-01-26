package com.ryuqq.application.classtemplate.fixture;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import java.util.List;

/**
 * CreateClassTemplateCommand Test Fixture
 *
 * @author development-team
 */
public final class CreateClassTemplateCommandFixture {

    private CreateClassTemplateCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateClassTemplateCommand defaultCommand() {
        return new CreateClassTemplateCommand(
                1L,
                1L, // classTypeId (AGGREGATE)
                "AGG-001",
                "^[A-Z]\\w+$",
                "Aggregate 템플릿",
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of());
    }
}
