-- 공개 피드 조회용 복합 인덱스
-- is_public(등가 필터) + id(정렬/커서)를 하나로 커버
-- 쿼리: WHERE is_public = TRUE [AND id < :lastId] ORDER BY id DESC
CREATE INDEX `idx_diary_public_id` ON `diary` (`is_public`, `id`);
