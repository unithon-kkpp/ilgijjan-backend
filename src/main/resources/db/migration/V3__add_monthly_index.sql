-- V3: 월별 내 일기 조회용 복합 인덱스
-- 쿼리: WHERE user_id = ? AND created_at >= ? AND created_at < ? ORDER BY created_at DESC
-- (user_id 등가) + (created_at 범위/정렬)을 한 인덱스로 커버 → filesort 제거.
-- 복합의 맨 앞이 user_id라 FK 인덱스 역할도 겸함 → 기존 단일 idx_diary_user_id는 중복이 되므로 제거.
--
-- 측정 메모: user당 일기 ~100개인 현실 분포에선 실이득 미미(웜 0.21ms→0.035ms, 둘 다 sub-ms).
-- filesort 제거·플랜 정리 목적 + 파워유저 대비 안전망으로 유지. (docs/쿼리 최적화/02-개선 측정 로그.md)
CREATE INDEX `idx_diary_user_created` ON `diary` (`user_id`, `created_at`);
DROP INDEX `idx_diary_user_id` ON `diary`;
