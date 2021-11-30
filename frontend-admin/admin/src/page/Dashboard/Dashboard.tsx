import React, { useState, useEffect } from 'react';
import { RunSqlResult } from '../../api';
import { apiController } from '../../apiController';
import SqlQueryResult from '../../component/SqlQueryResult/SqlQueryResult';

import './Dashboard.scss';

function Dashboard() {
  const [isLoading, setIsLoading] = useState(false);
  const [queryResult, setQueryResult] = useState<RunSqlResult | null>(null);

  useEffect(() => {
    withLoading(
      apiController.runSql(dashboardQuery)
        .then((result) => setQueryResult(result))
    );
  }, []);

  function withLoading(promise: Promise<any>): Promise<any> {
    setIsLoading(true);
    return promise.finally(() => setIsLoading(false));
  }

  return (
    <div>
      <h1>대시보드</h1>
      <div className="body">
        {queryResult ? <SqlQueryResult queryResult={queryResult} /> : null}
      </div>
    </div>
  );
}

export default Dashboard;

const dashboardQuery = `
select *
from
  (
  /* 동별 */
  select
    adr_dong as '동이름'
    , tag as '거점지'

    /* 시즌 전체 */
    , count(distinct building_id) as '전체 목표 빌딩 개수' /* cnt_target_building_id */
    , count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) + count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '전체 정복 빌딩 개수' /* 시즌1 정복 빌딩 개수 + 시즌2 정복 빌딩 개수 */
    , concat(round( (count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) + count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end)) * 100
                / count(distinct building_id) ,2), '%') as '전체 빌딩 달성률'
    , count(distinct store_id) as '전체 목표 점포 개수' /* cnt_target_store_id */
    , count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '전체 정복 점포 개수' /* 시즌1 정복 점포 개수 + 시즌2 정복 점포 개수 */
    , concat(round( (count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end)) * 100
                / count(distinct store_id) ,2), '%') as '전체 점포 달성률'
    /* 시즌1,2 빌딩 */
    , count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) as '시즌1 정복 빌딩 개수' /* cnt_season1_success_building_id */
    , concat(round(count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) * 100/ count(distinct building_id),2),'%') as '시즌1 빌딩 달성률(전체 타겟 빌딩 수 대비)'

    , count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) as '시즌2 목표 빌딩 개수'
    , count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '시즌2 정복 빌딩 개수'
    , concat(round(count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) * 100
                / count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) ,2),'%') as '시즌2 빌딩 달성률(시즌2 타겟 빌딩 수 대비)'
    /* 시즌1,2 점포 */
    , count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) as '시즌1 정복 점포 개수' /* cnt_season1_success_store_id */
    , concat(round(count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) * 100 / count(distinct store_id) ,2),'%') as '시즌1 점포 달성률(전체 타겟 점포 수 대비)'
    , count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) as '시즌2 목표 점포 개수'
    , count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '시즌2 정복 점포 개수'
    , concat(round(count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) * 100
                / count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) ,2),'%') as '시즌2 점포 달성률(시즌2 타겟 점포 수 대비)'
  from
    ( /* 시즌1정복결과 */
    select a.tag
        , a.adr_dong
        , a.building_id
        , case when (c.building_id is null or c.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_building_result'
        , case when (c.building_id is not null and c.created_at >= '2021-11-19 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_building_result'
        , a.store_id
        , a.store_name
        , a.category
        , case when (b.place_id is null or b.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_store_result'
        , case when (b.place_id is not null and b.created_at >= '2021-11-19 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_store_result'
    from our_map.target_stores a
    left outer join our_map.place_accessibility b on a.store_id = b.place_id
    left outer join our_map.building_accessibility c on a.building_id = c.building_id
    group by 1,2,3,4,5,6,7,8
    )t1
  group by 1,2
  union all
  /* 총합계 */
  select
      ' 합계' as '동이름'
    ,' 합계' as '거점지'
    , count(distinct building_id) as '(총계)전체 목표 빌딩 개수' /* cnt_target_building_id */
    , count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) + count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '전체 정복 빌딩 개수' /* 시즌1 정복 빌딩 개수 + 시즌2 정복 빌딩 개수 */
    , concat(round( (count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) + count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end)) * 100
                / count(distinct building_id) ,2), '%') as '전체 빌딩 달성률'
    , count(distinct store_id) as '전체 목표 점포 개수' /* cnt_target_store_id */
    , count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '전체 정복 점포 개수' /* 시즌1 정복 점포 개수 + 시즌2 정복 점포 개수 */
    , concat(round( (count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) + count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end)) * 100
                / count(distinct store_id) ,2), '%') as '전체 점포 달성률'
    /* 시즌1,2 빌딩 */
    , count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) as '시즌1 정복 빌딩 개수' /* cnt_season1_success_building_id */
    , concat(round(count(distinct case when ss1_target_building_result = '시즌1정복' then building_id end) * 100/ count(distinct building_id),2),'%') as '시즌1 빌딩 달성률(전체 타겟 빌딩 수 대비)'

    , count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) as '시즌2 목표 빌딩 개수'
    , count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) as '시즌2 정복 빌딩 개수'
    , concat(round(count(distinct case when ss2_target_building_result = '시즌2정복' then building_id end) * 100
                / count(distinct case when ss1_target_building_result = '시즌1미정복' then building_id end) ,2),'%') as '시즌2 빌딩 달성률(시즌2 타겟 빌딩 수 대비)'
    /* 시즌1,2 점포 */
    , count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) as '시즌1 정복 점포 개수' /* cnt_season1_success_store_id */
    , concat(round(count(distinct case when ss1_target_store_result = '시즌1정복' then store_id end) * 100 / count(distinct store_id) ,2),'%') as '시즌1 점포 달성률(전체 타겟 점포 수 대비)'
    , count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) as '시즌2 목표 점포 개수'
    , count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) as '시즌2 정복 점포 개수'
    , concat(round(count(distinct case when ss2_target_store_result = '시즌2정복' then store_id end) * 100
                / count(distinct case when ss1_target_store_result = '시즌1미정복' then store_id end) ,2),'%') as '시즌2 점포 달성률(시즌2 타겟 점포 수 대비)'
  from
    ( /* 시즌1정복결과 */
    select a.tag
        , a.adr_dong
        , a.building_id
        , case when (c.building_id is null or c.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_building_result'
        , case when (c.building_id is not null and c.created_at >= '2021-11-19 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_building_result'
        , a.store_id
        , a.store_name
        , a.category
        , case when (b.place_id is null or b.created_at >= '2021-11-19 00:00:00') then '시즌1미정복' else '시즌1정복' end as 'ss1_target_store_result'
        , case when (b.place_id is not null and b.created_at >= '2021-11-19 00:00:00') then '시즌2정복' else '시즌2미정복' end as 'ss2_target_store_result'
    from our_map.target_stores a
    left outer join our_map.place_accessibility b on a.store_id = b.place_id
    left outer join our_map.building_accessibility c on a.building_id = c.building_id
    group by 1,2,3,4,5,6,7,8
    )t1
  )tt
  order by 1,2
`;
