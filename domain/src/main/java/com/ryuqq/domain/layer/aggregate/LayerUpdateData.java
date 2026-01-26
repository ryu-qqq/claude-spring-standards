package com.ryuqq.domain.layer.aggregate;

import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;

/**
 * LayerUpdateData - 레이어 수정 데이터
 *
 * <p>레이어 수정 시 필요한 데이터를 담는 불변 객체입니다.
 *
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 */
public record LayerUpdateData(LayerCode code, LayerName name, String description, int orderIndex) {}
