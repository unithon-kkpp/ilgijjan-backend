-- 일기 생성 파이프라인 체크포인트 컬럼 추가
-- 실패한 단계만 재시도할 수 있도록 각 단계의 중간 결과를 저장.
-- 어느 단계가 필요한지는 별도 상태값 없이, 해당 필드가 비어있는지로 판단한다
-- (이미지·음악은 병렬 처리라 단일 "마지막 실패 단계" 값으로는 동시 실패를 표현할 수 없음)
ALTER TABLE `diary`
  ADD COLUMN `extracted_text` longtext AFTER `text`,
  ADD COLUMN `refined_text` longtext AFTER `extracted_text`,
  ADD COLUMN `retry_count` int NOT NULL DEFAULT 0;
