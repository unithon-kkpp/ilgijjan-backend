package com.ilgijjan.integration.common.config

import com.ilgijjan.common.constants.CacheConstants
import com.ilgijjan.domain.diary.application.DiaryReader
import com.ilgijjan.domain.diary.application.DiaryService
import com.ilgijjan.domain.diary.domain.Diary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class DiaryServiceCachingTest @Autowired constructor(
    private val diaryService: DiaryService,
    private val cacheManager: CacheManager
) {

    @MockitoBean
    private lateinit var diaryReader: DiaryReader

    private val emptySlice = SliceImpl<Diary>(emptyList(), PageRequest.of(0, 20), false)

    @BeforeEach
    fun clearCache() {
        cacheManager.getCache(CacheConstants.PUBLIC_DIARIES_CACHE)?.clear()
    }

    @Test
    fun `같은 파라미터로 두 번 조회하면 두 번째는 캐시에서 응답하고 DB를 다시 조회하지 않는다`() {
        // given
        `when`(diaryReader.findAllPublicWithSlice(null, 20)).thenReturn(emptySlice)

        // when
        diaryService.getPublicDiaries(null, 20)
        diaryService.getPublicDiaries(null, 20)

        // then
        verify(diaryReader, times(1)).findAllPublicWithSlice(null, 20)
    }

    @Test
    fun `size가 다르면 다른 캐시 키로 취급되어 각각 DB를 조회한다`() {
        // given
        val sliceOf10 = SliceImpl<Diary>(emptyList(), PageRequest.of(0, 10), false)
        `when`(diaryReader.findAllPublicWithSlice(null, 20)).thenReturn(emptySlice)
        `when`(diaryReader.findAllPublicWithSlice(null, 10)).thenReturn(sliceOf10)

        // when
        diaryService.getPublicDiaries(null, 20)
        diaryService.getPublicDiaries(null, 10)

        // then
        verify(diaryReader, times(1)).findAllPublicWithSlice(null, 20)
        verify(diaryReader, times(1)).findAllPublicWithSlice(null, 10)
    }

    @Test
    fun `캐시를 무효화하면 다음 조회 시 DB를 다시 조회한다`() {
        // given
        `when`(diaryReader.findAllPublicWithSlice(null, 20)).thenReturn(emptySlice)
        diaryService.getPublicDiaries(null, 20)

        // when
        cacheManager.getCache(CacheConstants.PUBLIC_DIARIES_CACHE)?.evict("0:20")
        diaryService.getPublicDiaries(null, 20)

        // then
        verify(diaryReader, times(2)).findAllPublicWithSlice(null, 20)
    }
}
