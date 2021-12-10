package job

import util.RunSqlService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EtlClosedStoreJob(
    private val runSqlService: RunSqlService
) : OurMapServerAdminJob {
    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun start() {
        executor.scheduleAtFixedRate(::run, 0L, 1L, TimeUnit.HOURS)
    }

    private fun run() {
        try {
            runSqlService.runNonSelectQuery("""
drop table our_map.closed_store;
            """.trimIndent())
        } catch (t: Throwable) {
            // 최초 실행 때는 테이블이 없어서 오류가 날 수도 있기 때문에, 에러를 무시한다.
        }
        runSqlService.runNonSelectQuery("""
create table our_map.closed_store as
(select *
from
    (select  aa.tag
           , aa.adr_dong
           /* , b.title */
           , aa.building_id
           , aa.store_id as closed_store_id
           , (case when aa.store_name = bb.가공_점포명1 then bb.가공_점포명1
                   when aa.store_name = bb.가공_점포명2 then bb.가공_점포명2
                   when aa.store_name = bb.가공_점포명3 then bb.가공_점포명3
                   when aa.store_name = bb.가공_점포명4 then bb.가공_점포명4
                   else bb.점포명 end) as '점포명'
           , bb.퀘스트_빌딩경도
           , bb.퀘스트_빌딩위도
           , bb.is_closed
           , bb.quest_created_at
    from our_map.target_stores aa
    inner join
        (SELECT quest_title as title
              , truncate(quest_target_lng,5) as '퀘스트_빌딩경도'
              , truncate(quest_target_lat,5) as '퀘스트_빌딩위도'
              , quest_target_place_name as '점포명' /* distinct 점포명 시 921개 */
              , substring_index(quest_target_place_name,' ',-1) as '가공_점포명1'
              , substring_index(quest_target_place_name,' ',-2) as '가공_점포명2'
                      , substring_index(quest_target_place_name,' ',-3) as '가공_점포명3'
                      , substring_index(quest_target_place_name,' ',-4) as '가공_점포명4'
              , (case when is_closed = 1 then '폐점' end) as is_closed
              , quest_created_at
         FROM our_map.club_quest_result
         where is_closed = 1
         )bb on ((aa.store_name = bb.점포명) or (aa.store_name = bb.가공_점포명1) or (aa.store_name = bb.가공_점포명2) or (aa.store_name = bb.가공_점포명3) or (aa.store_name = bb.가공_점포명4))
    )t1
left outer join our_map.building t2 on (t1.building_id = t2.id)
where concat((case when t1.점포명 = '전' then '127.14555' /* 오류 수동 정정 */
                   when t1.점포명 = '동경규동 수내점' then '127.11577'
                   when t1.점포명 = '하루요거' then '127.1162187'
                   else t1.퀘스트_빌딩경도 end),
              (case when t1.점포명 = '동경규동 수내점' then '37.37907'
                   when t1.점포명 = '하루요거트' then '37.37935'
                   else t1.퀘스트_빌딩위도 end)) = concat(truncate(t2.lng,5), truncate(t2.lat,5))
);
        """.trimIndent())
    }
}
