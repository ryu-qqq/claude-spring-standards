package com.ryuqq.domain.classtypecategory.aggregate;

import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;

/**
 * ClassTypeCategoryUpdateData - 클래스 타입 카테고리 수정 데이터
 *
 * <p>카테고리 업데이트에 필요한 데이터를 담는 불변 레코드입니다.
 *
 * @author ryu-qqq
 */
public record ClassTypeCategoryUpdateData(
        CategoryCode code, CategoryName name, String description, int orderIndex) {

    public static ClassTypeCategoryUpdateData of(
            CategoryCode code, CategoryName name, String description, int orderIndex) {
        return new ClassTypeCategoryUpdateData(code, name, description, orderIndex);
    }
}
