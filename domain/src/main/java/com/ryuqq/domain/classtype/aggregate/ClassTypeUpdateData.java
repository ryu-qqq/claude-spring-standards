package com.ryuqq.domain.classtype.aggregate;

import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;

/**
 * ClassTypeUpdateData - 클래스 타입 수정 데이터
 *
 * <p>클래스 타입 업데이트에 필요한 데이터를 담는 불변 레코드입니다.
 *
 * @author ryu-qqq
 */
public record ClassTypeUpdateData(
        ClassTypeCode code, ClassTypeName name, String description, int orderIndex) {

    public static ClassTypeUpdateData of(
            ClassTypeCode code, ClassTypeName name, String description, int orderIndex) {
        return new ClassTypeUpdateData(code, name, description, orderIndex);
    }
}
