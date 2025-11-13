package com.ryuqq.domain.member;

/**
 * 회원 상태
 *
 * @author ryuqq
 * @since 1.0
 */
public enum MemberStatus {
    /**
     * 활성
     */
    ACTIVE,

    /**
     * 비활성
     */
    INACTIVE,

    /**
     * 잠금 (로그인 5회 실패)
     */
    LOCKED,

    /**
     * 탈퇴
     */
    WITHDRAWN;

    /**
     * 다른 상태로 전이 가능한지 확인
     *
     * @param targetStatus 전이하려는 상태
     * @return 전이 가능 여부
     */
    public boolean canTransitionTo(MemberStatus targetStatus) {
        if (this == targetStatus) {
            return false; // 같은 상태로는 전이 불가
        }

        return switch (this) {
            case ACTIVE -> targetStatus == INACTIVE || targetStatus == LOCKED || targetStatus == WITHDRAWN;
            case INACTIVE -> targetStatus == ACTIVE || targetStatus == WITHDRAWN;
            case LOCKED -> false; // 잠금 상태는 관리자만 해제 가능 (별도 메서드 필요)
            case WITHDRAWN -> false; // 탈퇴 상태는 최종 상태
        };
    }
}
