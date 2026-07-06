-- V2: 공개 피드 조회용 복합 인덱스
-- 쿼리: WHERE is_public = TRUE ORDER BY id DESC LIMIT n  (커서 페이지네이션)
-- (is_public 등가 필터) + (id 정렬)을 인덱스 하나로 커버. SELECT가 id면 커버링 인덱스로 동작.
-- 측정: 공개 0.3% 저밀도에서 10,106행 스캔 → 20행 (docs/쿼리 최적화/02-개선 측정 로그.md)
CREATE INDEX `idx_diary_public_id` ON `diary` (`is_public`, `id`);
