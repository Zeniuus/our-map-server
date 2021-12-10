import { Spinner } from '@blueprintjs/core';
import React, { useState, useEffect } from 'react';
import { RunSqlResult } from '../../api';
import { apiController } from '../../apiController';
import SqlQueryResultComponent from '../../component/SqlQueryResult/SqlQueryResultComponent';

import './DashboardPage.scss';

function DashboardPage() {
  const loadingTextBase = '시간이 좀 걸릴 수 있습니다';
  const [loadingTextSpinner, setLoadingTextSpinner] = useState('.');
  const [loadingText, setLoadingText] = useState(`${loadingTextBase}${loadingTextSpinner}`);
  const [loadingTextUpdater, setLoadingTextUpdater] = useState<any>();
  useEffect(() => {
    const localLoadingTextUpdater = setTimeout(() => {
      const nextSpinnerLength = (loadingTextSpinner.length % 3) + 1;
      const nextLoadingTextSpinner = '.'.repeat(nextSpinnerLength);
      setLoadingTextSpinner(nextLoadingTextSpinner);
      setLoadingText(`${loadingTextBase}${nextLoadingTextSpinner}`);
    }, 400);
    setLoadingTextUpdater(localLoadingTextUpdater);

    return () => {
      clearTimeout(localLoadingTextUpdater);
    }
  }, [loadingText]);

  const [queryResult, setQueryResult] = useState<RunSqlResult | null>(null);
  useEffect(() => {
    apiController.runSql(dashboardQuery)
      .then((result) => {
        setQueryResult(result);
        clearInterval(loadingTextUpdater);
      });
  }, []);

  return (
    <div>
      <h1>대시보드</h1>
      <div className="body">
        {
          queryResult
            ? <SqlQueryResultComponent queryResult={queryResult} />
            : (
              <div className="loading-container">
                <Spinner />
                <div className="loading-text">{loadingText}</div>
              </div>
            )
        }
      </div>
    </div>
  );
}

export default DashboardPage;

const dashboardQuery = `
select *
from
	(
	/* 동별 */
	select 
		adr_dong as '동이름'
		, tag as '거점지'
		
		/* 시즌 전체 */

		/* 빌딩 */
		, count(distinct building_id) as '전체 목표 빌딩 개수' /* cnt_target_building_id */
		, count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) 
				+ count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) 
				+ count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end)
				as '전체 정복 빌딩 개수' /* 시즌1 정복 빌딩 개수 + 시즌2 정복 빌딩 개수 */
		, concat(round( (count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) 
				+ count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end)
				+ count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end)
				) * 100
			    / count(distinct building_id) ,2), '%') as '전체 빌딩 달성률'

		/* 점포 */
		/* 점포 - 폐점 제외 */
		, count(distinct store_id) as '전체 목표 점포 개수' /* cnt_target_store_id */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
		       + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) 
		       + count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end)
		       as '전체 정복 점포 개수(폐점 제외)' /* 시즌1 정복 점포 개수 + 시즌2 정복 점포 개수 */
		, concat(round((count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
			    + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end)
			    + count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end)
			    ) * 100
			    / count(distinct store_id) ,2), '%') 
		        as '전체 점포 달성률(폐점 제외)'
		 /*점포 - 폐점 */
		, count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end) 
				+ count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) 
			as '전체 정복 점포 개수(폐점)'
		, concat(round( (count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end)
						+ count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) 
						) * 100
			          / count(distinct store_id) ,2), '%') as '전체 점포 달성률(폐점)'
		/* 점포 - 폐점 포함 */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
				+ count(distinct case when ss2_target_store_result in ('시즌2정복', '시즌2폐점') then store_id end) 
				+ count(distinct case when ss3_target_store_result in ('시즌3정복', '시즌3폐점') then store_id end)
				as '전체 정복 점포 개수(폐점 포함)'
		, concat(round( (count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
				+ count(distinct case when ss2_target_store_result in ('시즌2정복', '시즌2폐점') then store_id end)
				+ count(distinct case when ss3_target_store_result in ('시즌3정복', '시즌3폐점') then store_id end)
				) * 100
			    / count(distinct store_id) ,2), '%') 
				as '전체 점포 달성률(폐점 포함)'


		/* 시즌별 빌딩 상세*/
		/* 시즌1 */
		, count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) as '시즌1 정복 빌딩 개수' /* cnt_season1_success_building_id */ 
		, concat(round(count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) * 100/ count(distinct building_id),2),'%') as '시즌1 빌딩 달성률(전체 타겟 빌딩 수 대비)'
		/* 시즌2 */
		, count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) as '시즌2 목표 빌딩 개수'
		, count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '시즌2 정복 빌딩 개수' 
		, concat(round(count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) * 100
			          / count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) ,2),'%') as '시즌2 빌딩 달성률(시즌 타겟 빌딩 수 대비)'
		/* 시즌2 겨울방학 */
		, count(distinct case when ss1_target_building_result = '시즌1미정복' and ss2_target_building_result = '시즌2미정복' then building_id end) as '시즌3 목표 빌딩 개수'
		, count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end) as '시즌3 정복 빌딩 개수' 
		, concat(round(count(distinct case when ss2_target_building_result = '시즌3정복' then building_id end) * 100
			          / count(distinct case when ss1_target_building_result = '시즌1미정복' and ss2_target_building_result = '시즌2미정복' then building_id end) ,2),'%') as '시즌3 빌딩 달성률(시즌 타겟 빌딩 수 대비)'

		/* 시즌별 점포 상세*/
		/* 시즌1 */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) as '시즌1 정복 점포 개수' /* cnt_season1_success_store_id */
		, concat(round(count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) * 100 / count(distinct store_id) ,2),'%') as '시즌1 점포 달성률(전체 타겟 점포 수 대비)'
		/* 시즌2 */
		, count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) as '시즌2 목표 점포 개수'
		, count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '시즌2 정복 점포 개수(폐점 제외)' 
		, count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end) as '시즌2 정복 점포 개수(폐점)'
		, count(distinct case when ss2_target_store_result in ('시즌2정복','시즌2폐점') then store_id end) as '시즌2 정복 점포 개수(폐점 포함)'
		, concat(round(count(distinct case when ss2_target_store_result in ('시즌2정복','시즌2폐점') then store_id end) * 100
			          / count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) ,2),'%') as '시즌2 점포 달성률(시즌 타겟 점포 수 대비)-폐점포함'
		/* 시즌3 */
		, count(distinct case when ss1_target_store_result = '시즌1미정복' and ss2_target_store_result = '시즌2미정복' then store_id end) as '시즌3 목표 점포 개수'
		, count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end) as '시즌3 정복 점포 개수(폐점 제외)' 
		, count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) as '시즌3 정복 점포 개수(폐점)'
		, count(distinct case when ss3_target_store_result in ('시즌3정복','시즌3폐점') then store_id end) as '시즌3 정복 점포 개수(폐점 포함)'
		, concat(round(count(distinct case when ss3_target_store_result in ('시즌3정복','시즌3폐점') then store_id end) * 100
			          / count(distinct case when ss1_target_store_result = '시즌1미정복' and ss2_target_store_result = '시즌2미정복' then store_id end) ,2),'%') as '시즌3 점포 달성률(시즌 타겟 점포 수 대비)-폐점포함'		
		
	from 
		( /* 시즌1정복결과 */
		select a.tag
			 , a.adr_dong
			 /* 빌딩 */
			 , a.building_id
			 , case when (c.building_id is null or c.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_building_result'
			 , case when (c.building_id is not null and c.created_at >= '2021-11-19 00:00:00' and c.created_at < '2021-12-13 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_building_result'
			 , case when (c.building_id is not null and c.created_at >= '2021-12-13 00:00:00') then '시즌3정복' else '시즌3미정복' end as 'ss3_target_building_result'
			 /* 점포 */
			 , a.store_id
			 , a.store_name
			 , a.category
			 /* 점포- 시즌1 */
			 , case when (b.place_id is null or b.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_store_result'
			 /* 점포 - 시즌2 */
			 , case when (b.place_id is not null and b.created_at >= '2021-11-19 00:00:00' and b.created_at < '2021-12-13 00:00:00') then '시즌2정복' 
			        when (b.place_id is null and d.점포명 is not null and d.quest_created_at < '2021-12-13 00:00:00') then '시즌2폐점'
			        else '시즌2미정복' end as 'ss2_target_store_result'
			 /* 점포 - 시즌2 겨울방학 */
			 , case when (b.place_id is not null and b.created_at >= '2021-12-13 00:00:00') then '시즌3정복' 
			        when (b.place_id is null and d.점포명 is not null and d.quest_created_at >= '2021-12-13 00:00:00') then '시즌3폐점'
			        else '시즌3미정복' end as 'ss3_target_store_result'
		from our_map.target_stores a
		left outer join our_map.place_accessibility b on a.store_id = b.place_id
		left outer join our_map.building_accessibility c on a.building_id = c.building_id
		left outer join our_map.closed_store d on a.store_id = d.closed_store_id
		group by 1,2,3,4,5,6,7,8,9,10
		)t1
	group by 1,2

	union all 

	/* 총합계 */
	select
		 ' 합계' as '동이름'
		,' 합계' as '거점지'
		
		/* 시즌 전체 */

		/* 빌딩 */
		, count(distinct building_id) as '전체 목표 빌딩 개수' /* cnt_target_building_id */
		, count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) 
				+ count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) 
				+ count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end)
				as '전체 정복 빌딩 개수' /* 시즌1 정복 빌딩 개수 + 시즌2 정복 빌딩 개수 */
		, concat(round( (count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) 
				+ count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end)
				+ count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end)
				) * 100
			    / count(distinct building_id) ,2), '%') as '전체 빌딩 달성률'

		/* 점포 */
		/* 점포 - 폐점 제외 */
		, count(distinct store_id) as '전체 목표 점포 개수' /* cnt_target_store_id */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
		       + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) 
		       + count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end)
		       as '전체 정복 점포 개수(폐점 제외)' /* 시즌1 정복 점포 개수 + 시즌2 정복 점포 개수 */
		, concat(round((count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
			    + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end)
			    + count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end)
			    ) * 100
			    / count(distinct store_id) ,2), '%') 
		        as '전체 점포 달성률(폐점 제외)'
		 /*점포 - 폐점 */
		, count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end) 
				+ count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) 
			as '전체 정복 점포 개수(폐점)'
		, concat(round( (count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end)
						+ count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) 
						) * 100
			          / count(distinct store_id) ,2), '%') as '전체 점포 달성률(폐점)'
		/* 점포 - 폐점 포함 */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
				+ count(distinct case when ss2_target_store_result in ('시즌2정복', '시즌2폐점') then store_id end) 
				+ count(distinct case when ss3_target_store_result in ('시즌3정복', '시즌3폐점') then store_id end)
				as '전체 정복 점포 개수(폐점 포함)'
		, concat(round( (count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) 
				+ count(distinct case when ss2_target_store_result in ('시즌2정복', '시즌2폐점') then store_id end)
				+ count(distinct case when ss3_target_store_result in ('시즌3정복', '시즌3폐점') then store_id end)
				) * 100
			    / count(distinct store_id) ,2), '%') 
				as '전체 점포 달성률(폐점 포함)'


		/* 시즌별 빌딩 상세*/
		/* 시즌1 */
		, count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) as '시즌1 정복 빌딩 개수' /* cnt_season1_success_building_id */ 
		, concat(round(count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) * 100/ count(distinct building_id),2),'%') as '시즌1 빌딩 달성률(전체 타겟 빌딩 수 대비)'
		/* 시즌2 */
		, count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) as '시즌2 목표 빌딩 개수'
		, count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '시즌2 정복 빌딩 개수' 
		, concat(round(count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) * 100
			          / count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) ,2),'%') as '시즌2 빌딩 달성률(시즌 타겟 빌딩 수 대비)'
		/* 시즌2 겨울방학 */
		, count(distinct case when ss1_target_building_result = '시즌1미정복' and ss2_target_building_result = '시즌2미정복' then building_id end) as '시즌3 목표 빌딩 개수'
		, count(distinct case when ss3_target_building_result = '시즌3정복' then building_id end) as '시즌3 정복 빌딩 개수' 
		, concat(round(count(distinct case when ss2_target_building_result = '시즌3정복' then building_id end) * 100
			          / count(distinct case when ss1_target_building_result = '시즌1미정복' and ss2_target_building_result = '시즌2미정복' then building_id end) ,2),'%') as '시즌3 빌딩 달성률(시즌 타겟 빌딩 수 대비)'

		/* 시즌별 점포 상세*/
		/* 시즌1 */
		, count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) as '시즌1 정복 점포 개수' /* cnt_season1_success_store_id */
		, concat(round(count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) * 100 / count(distinct store_id) ,2),'%') as '시즌1 점포 달성률(전체 타겟 점포 수 대비)'
		/* 시즌2 */
		, count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) as '시즌2 목표 점포 개수'
		, count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '시즌2 정복 점포 개수(폐점 제외)' 
		, count(distinct case when ss2_target_store_result = '시즌2폐점' then store_id end) as '시즌2 정복 점포 개수(폐점)'
		, count(distinct case when ss2_target_store_result in ('시즌2정복','시즌2폐점') then store_id end) as '시즌2 정복 점포 개수(폐점 포함)'
		, concat(round(count(distinct case when ss2_target_store_result in ('시즌2정복','시즌2폐점') then store_id end) * 100
			          / count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) ,2),'%') as '시즌2 점포 달성률(시즌 타겟 점포 수 대비)-폐점포함'
		/* 시즌3 */
		, count(distinct case when ss1_target_store_result = '시즌1미정복' and ss2_target_store_result = '시즌2미정복' then store_id end) as '시즌3 목표 점포 개수'
		, count(distinct case when ss3_target_store_result = '시즌3정복' then store_id end) as '시즌3 정복 점포 개수(폐점 제외)' 
		, count(distinct case when ss3_target_store_result = '시즌3폐점' then store_id end) as '시즌3 정복 점포 개수(폐점)'
		, count(distinct case when ss3_target_store_result in ('시즌3정복','시즌3폐점') then store_id end) as '시즌3 정복 점포 개수(폐점 포함)'
		, concat(round(count(distinct case when ss3_target_store_result in ('시즌3정복','시즌3폐점') then store_id end) * 100
			          / count(distinct case when ss1_target_store_result = '시즌1미정복' and ss2_target_store_result = '시즌2미정복' then store_id end) ,2),'%') as '시즌3 점포 달성률(시즌 타겟 점포 수 대비)-폐점포함'		
		
	from 
		( /* 시즌1정복결과 */
		select a.tag
			 , a.adr_dong
			 /* 빌딩 */
			 , a.building_id
			 , case when (c.building_id is null or c.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_building_result'
			 , case when (c.building_id is not null and c.created_at >= '2021-11-19 00:00:00' and c.created_at < '2021-12-13 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_building_result'
			 , case when (c.building_id is not null and c.created_at >= '2021-12-13 00:00:00') then '시즌3정복' else '시즌3미정복' end as 'ss3_target_building_result'
			 /* 점포 */
			 , a.store_id
			 , a.store_name
			 , a.category
			 /* 점포- 시즌1 */
			 , case when (b.place_id is null or b.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_store_result'
			 /* 점포 - 시즌2 */
			 , case when (b.place_id is not null and b.created_at >= '2021-11-19 00:00:00' and b.created_at < '2021-12-13 00:00:00') then '시즌2정복' 
			        when (b.place_id is null and d.점포명 is not null and d.quest_created_at < '2021-12-13 00:00:00') then '시즌2폐점'
			        else '시즌2미정복' end as 'ss2_target_store_result'
			 /* 점포 - 시즌2 겨울방학 */
			 , case when (b.place_id is not null and b.created_at >= '2021-12-13 00:00:00') then '시즌3정복' 
			        when (b.place_id is null and d.점포명 is not null and d.quest_created_at >= '2021-12-13 00:00:00') then '시즌3폐점'
			        else '시즌3미정복' end as 'ss3_target_store_result'
		from our_map.target_stores a
		left outer join our_map.place_accessibility b on a.store_id = b.place_id
		left outer join our_map.building_accessibility c on a.building_id = c.building_id
		left outer join our_map.closed_store d on a.store_id = d.closed_store_id
		group by 1,2,3,4,5,6,7,8,9,10
		)t1
	group by 1,2
	)tt
	order by 1,2
`;
