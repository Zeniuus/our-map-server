import React, { useState, useEffect } from 'react';
import { Button, ButtonGroup, Checkbox } from '@blueprintjs/core';
import apiClient from '../../apiClient';
import { ClubQuestContentTargetDTO, ClubQuestContentTargetPlaceDTO, ClubQuestDTO, LocationDTO } from '../../type';
import { determineCenter, determineLevel } from '../../util/kakaoMap';

import './ClubQuest.scss';

declare global {
  interface Window {
    kakao: any;
  }
}

interface ClubQuestProps {
  match: {
    params: {
      id: string;
    }
  }
}

function ClubQuest(props: ClubQuestProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [clubQuest, setClubQuest] = useState<ClubQuestDTO | null>(null);
  const [currentLocation, setCurrentLocation] = useState<LocationDTO | null>(null);
  const [map, setMap] = useState<any>(null);

  useEffect(() => {
    getClubQuest(props.match.params.id)
      .then((res) => {
        const clubQuest: ClubQuestDTO = res.data;
        setClubQuest(clubQuest);
        installMap(clubQuest);
      });
  }, []);
  useEffect(() => {
    const timeout = setTimeout(() => {
      getClubQuest(props.match.params.id)
        .then((res) => {
          const clubQuest: ClubQuestDTO = res.data;
          setClubQuest(clubQuest);
        });
    }, 10000);
    return () => { clearTimeout(timeout); };
  }, [clubQuest]);

  function getClubQuest(id: string): Promise<any> {
    setIsLoading(true);
    return apiClient.get(`/clubQuests/${id}`)
      .finally(() => {
        setIsLoading(false);
      });
  }

  function setPlaceIsCompleted(id: string, target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO): Promise<any> {
    setIsLoading(true);
    return apiClient.post(`/clubQuests/${id}/setPlaceIsCompleted/${!place.isCompleted}`, {
      lng: target.lng,
      lat: target.lat,
      targetDisplayedName: target.displayedName,
      placeName: place.name,
    }).then((res) => {
      const clubQuest: ClubQuestDTO = res.data;
      setClubQuest(clubQuest);
    }).finally(() => {
      setIsLoading(false);
    });
  }

  function setPlaceIsClosed(id: string, target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO): Promise<any> {
    setIsLoading(true);
    return apiClient.post(`/clubQuests/${id}/setPlaceIsClosed/${!place.isClosed}`, {
      lng: target.lng,
      lat: target.lat,
      targetDisplayedName: target.displayedName,
      placeName: place.name,
    }).then((res) => {
      const clubQuest: ClubQuestDTO = res.data;
      setClubQuest(clubQuest);
    }).finally(() => {
      setIsLoading(false);
    });
  }

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
      setPlaceIsCompleted(clubQuest!.id, target, place);
    };
  }

  const onPlaceIsClosedChange = (target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO) => {
    return () => {
      setPlaceIsClosed(clubQuest!.id, target, place);
    };
  }

  return (
    <div>
      <h1>{clubQuest?.title}</h1>
      <div id="map" style={{ minWidth: '320px', minHeight: '500px' }} />
      <div className="map-manipulate-button-div">
        <ButtonGroup className="map-manipulate-button-container">
          {clubQuest != null ? <Button text="퀘스트 전체 표시하기" onClick={showQuestsOnMap}></Button> : null}
          {currentLocation != null ? <Button text="현재 위치 표시하기" onClick={showCurrentLocationOnMap}></Button> : null}
        </ButtonGroup>
      </div>
      {
        clubQuest
          ? (
            <table className="bp3-html-table bp3-html-table-bordered bp3-html-table-condensed bp3-interactive">
              <thead>
                <tr>
                  <th className="title-column">건물 이름</th>
                  <th>장소 이름</th>
                  <th>정복</th>
                  <th>폐업</th>
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

export default ClubQuest;
