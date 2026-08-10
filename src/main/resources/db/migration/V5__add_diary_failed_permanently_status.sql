-- 재시도해도 성공할 수 없는 실패(NonRetryableException)를 일반 실패와 구분하기 위한 상태값 추가
-- 추후 배치 재시도 작업이 FAILED만 재시도 대상으로 삼고 FAILED_PERMANENTLY는 영구 제외하도록 함
ALTER TABLE `diary`
  MODIFY COLUMN `status` enum('PENDING','COMPLETED','FAILED','FAILED_PERMANENTLY','DELETED') NOT NULL;
