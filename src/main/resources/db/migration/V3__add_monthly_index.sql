-- 월별 조회용 복합 인덱스
-- user_id(등가 필터) + created_at(범위/정렬)을 하나로 커버 → filesort 제거
-- 쿼리: WHERE user_id = ? AND created_at >= ? AND created_at < ? ORDER BY created_at DESC
CREATE INDEX `idx_diary_user_created` ON `diary` (`user_id`, `created_at`);
