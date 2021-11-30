import React, { useState, useEffect } from 'react';
import { Button, ButtonGroup, Checkbox } from '@blueprintjs/core';
import { ClubQuestContentTargetDTO, ClubQuestContentTargetPlaceDTO, ClubQuestDTO, LocationDTO } from '../../type';
import { determineCenter, determineLevel } from '../../util/kakaoMap';
import { apiController } from '../../apiController';

import './ClubQuestPage.scss';

declare global {
  interface Window {
    kakao: any;
  }
}

interface ClubQuestPageProps {
  match: {
    params: {
      id: string;
    }
  }
}

function ClubQuestPage(props: ClubQuestPageProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [clubQuest, setClubQuest] = useState<ClubQuestDTO | null>(null);
  const [currentLocation, setCurrentLocation] = useState<LocationDTO | null>(null);
  const [map, setMap] = useState<any>(null);

  function withLoading(promise: Promise<any>): Promise<any> {
    setIsLoading(true);
    return promise.finally(() => setIsLoading(false));
  }

  useEffect(() => {
    withLoading(
      apiController.getClubQuest(props.match.params.id)
        .then((clubQuest) => {
          setClubQuest(clubQuest);
          installMap(clubQuest);
        })
    );
  }, []);
  useEffect(() => {
    const timeout = setTimeout(() => {
      withLoading(
        apiController.getClubQuest(props.match.params.id)
          .then(clubQuest => setClubQuest(clubQuest))
      );
    }, 10000);
    return () => { clearTimeout(timeout); };
  }, [clubQuest]);

  function installMap(clubQuest: ClubQuestDTO) {
    if (clubQuest != null && map == null) {
      const container = document.getElementById('map');
      const center = determineCenter(clubQuest.content.targets);
      const options = {
        center: new window.kakao.maps.LatLng(center.lat, center.lng),
        level: determineLevel(clubQuest.content.targets),
      };
      const map = new window.kakao.maps.Map(container, options);
      setMap(map);

      clubQuest.content.targets.forEach((target) => {
        const marker = new window.kakao.maps.Marker({
          position: new window.kakao.maps.LatLng(target.lat, target.lng),
          clickable: true, // 마커를 클릭했을 때 지도의 클릭 이벤트가 발생하지 않도록 설정합니다.
        });
        marker.setMap(map);

        const tooltip = new window.kakao.maps.InfoWindow({
          content : `<div style="padding:5px;">${target.displayedName}</div>`,
          removable: true
        });
        window.kakao.maps.event.addListener(marker, 'click', () => {
          tooltip.open(map, marker);
        });
      });

      if (navigator.geolocation != null) {
        let currentLocationMarker: any = null;
        const updateCurrentLocationMarker = () => {
          navigator.geolocation.getCurrentPosition((position) => {
            setCurrentLocation({ lat: position.coords.latitude, lng: position.coords.longitude });

            if (currentLocationMarker != null) {
              currentLocationMarker.setMap(null);
            }

            const markerImage = new window.kakao.maps.MarkerImage(
              '/currentLocation.jpg',
              new window.kakao.maps.Size(24, 24),
              { offset: new window.kakao.maps.Point(12, 12) },
            );
            currentLocationMarker = new window.kakao.maps.Marker({
              position: new window.kakao.maps.LatLng(position.coords.latitude, position.coords.longitude),
              image: markerImage,
              clickable: true, // 마커를 클릭했을 때 지도의 클릭 이벤트가 발생하지 않도록 설정합니다.
            });
            currentLocationMarker.setMap(map);
          });
        }

        updateCurrentLocationMarker();
        setInterval(updateCurrentLocationMarker, 5000);
      }
    }
  }

  const showQuestsOnMap = () => {
    if (clubQuest != null) {
      const center = determineCenter(clubQuest.content.targets);
      map.setLevel(determineLevel(clubQuest.content.targets));
      map.panTo(new window.kakao.maps.LatLng(center.lat, center.lng));
    }
  };
  const showCurrentLocationOnMap = () => {
    if (clubQuest != null && currentLocation != null) {
      map.panTo(new window.kakao.maps.LatLng(currentLocation?.lat, currentLocation?.lng));
    }
  }

  const onPlaceIsCompletedChange = (target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO) => {
    return () => {
      withLoading(
        apiController.setPlaceIsCompleted(clubQuest!.id, target, place)
          .then((clubQuest) => setClubQuest(clubQuest))
      );
    };
  }

  const onPlaceIsClosedChange = (target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO) => {
    return () => {
      withLoading(
        apiController.setPlaceIsClosed(clubQuest!.id, target, place)
          .then((clubQuest) => setClubQuest(clubQuest))
      );
    };
  }

  const onPlaceIsNotAccessibleChange = (target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO) => {
    return () => {
      withLoading(
        apiController.setPlaceIsNotAccessible(clubQuest!.id, target, place)
          .then((clubQuest) => setClubQuest(clubQuest))
      );
    };
  }

  return (
    <div>
      <h1>{clubQuest?.title}</h1>
      <div id="map" style={{ minWidth: '200px', height: 'calc(100vw - 40px)', maxHeight: '500px' }} />
      <div className="map-manipulate-button-div">
        <ButtonGroup className="map-manipulate-button-container">
          {clubQuest != null ? <Button text="퀘스트 전체 표시하기" onClick={showQuestsOnMap}></Button> : null}
          {currentLocation != null ? <Button text="현재 위치 표시하기" onClick={showCurrentLocationOnMap}></Button> : null}
        </ButtonGroup>
      </div>
      <p>
        ※ 폐업 여부는 '네이버 지도'로 검색해 확인하시면 편리합니다
      </p>
      {
        clubQuest
          ? (
            <table className="bp3-html-table bp3-html-table-bordered bp3-html-table-condensed bp3-interactive">
              <thead>
                <tr>
                  <th className="title-column">건물</th>
                  <th>점포 또는 매장</th>
                  <th>정복</th>
                  <th>폐업</th>
                  <th>접근 불가</th>
                </tr>
              </thead>
              <tbody>
                {
                  clubQuest.content.targets.flatMap((target) => {
                    return target.places.map((place, idx) => {
                      return (
                        <tr>
                          <td>{idx === 0 ? target.displayedName : ''}</td>
                          <td>{place.name}</td>
                          <td><Checkbox checked={place.isCompleted} disabled={isLoading} large onChange={onPlaceIsCompletedChange(target, place)} /></td>
                          <td><Checkbox checked={place.isClosed} disabled={isLoading} large onChange={onPlaceIsClosedChange(target, place)} /></td>
                          <td><Checkbox checked={place.isNotAccessible} disabled={isLoading} large onChange={onPlaceIsNotAccessibleChange(target, place)} /></td>
                        </tr>
                      );
                    });
                  })
                }
              </tbody>
            </table>
          )
          : null
      }
    </div>
  );
}

export default ClubQuestPage;
