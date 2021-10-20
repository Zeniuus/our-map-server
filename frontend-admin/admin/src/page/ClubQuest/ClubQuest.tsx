import React, { useState, useEffect } from 'react';
import { Button, ButtonGroup } from '@blueprintjs/core';
import haversine from 'haversine';
import apiClient from '../../apiClient';
import { Location } from '../../type';

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
  const [clubQuest, setClubQuest] = useState<ClubQuest | null>(null);
  const [currentLocation, setCurrentLocation] = useState<Location | null>(null);
  const [map, setMap] = useState<any>(null);

  useEffect(() => {
    getClubQuest(props.match.params.id);
  }, []);

  function getClubQuest(id: string): Promise<any> {
    return apiClient.get(`/clubQuests/${id}`)
      .then((res) => {
        const clubQuest: ClubQuest = res.data;
        setClubQuest(clubQuest);
        installMap(clubQuest);
      });
  }

  function installMap(clubQuest: ClubQuest) {
    if (clubQuest != null) {
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

  function determineCenter(locations: Location[]): Location {
    if (locations.length === 0) {
      return {
        lng: 137,
        lat: 37,
      };
    }
    const minLng = min(locations.map(it => it.lng));
    const maxLng = max(locations.map(it => it.lng));
    const minLat = min(locations.map(it => it.lat));
    const maxLat = max(locations.map(it => it.lat));
    return {
      lng: (minLng! + maxLng!) / 2,
      lat: (minLat! + maxLat!) / 2,
    };
  }

  function determineLevel(locations: Location[]): number {
    // 척도
    // 3: 50m
    // 4: 100m
    // 5: 250m
    // 6: 500m
    // 7: 1km
    // 8: 2km
    // 9: 4km
    if (locations.length <= 1) {
      return 3;
    }
    const minLng = min(locations.map(it => it.lng))!;
    const maxLng = max(locations.map(it => it.lng))!;
    const minLat = min(locations.map(it => it.lat))!;
    const maxLat = max(locations.map(it => it.lat))!;
    const maxDistance = haversine({ longitude: minLng, latitude: minLat}, { longitude: maxLng, latitude: maxLat }, { unit: 'meter' });
    if (maxDistance < 200) return 3;
    if (maxDistance < 400) return 4;
    if (maxDistance < 1000) return 5;
    if (maxDistance < 2000) return 6;
    if (maxDistance < 4000) return 7;
    if (maxDistance < 8000) return 8;
    if (maxDistance < 16000) return 9;
    return 10;
  }

  function min<T>(values: Array<T>): T | null {
    if (values.length == null) {
      return null;
    }
    let min = values[0];
    for (let i = 1; i < values.length; i += 1) {
      if (values[i] < min) {
        min = values[i];
      }
    }
    return min;
  }

  function max<T>(values: Array<T>): T | null {
    if (values.length == null) {
      return null;
    }
    let max = values[0];
    for (let i = 1; i < values.length; i += 1) {
      if (values[i] > max) {
        max = values[i];
      }
    }
    return max;
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

  return (
    <div>
      <h1>{clubQuest?.title}</h1>
      <div id="map" style={{ minWidth: '320px', minHeight: '500px' }} />
      <ButtonGroup className="map-manipulate-button-container">
        {clubQuest != null ? <Button text="퀘스트 전체 표시하기" onClick={showQuestsOnMap}></Button> : null}
        {currentLocation != null ? <Button text="현재 위치 표시하기" onClick={showCurrentLocationOnMap}></Button> : null}
      </ButtonGroup>
    </div>
  );
}

export default ClubQuest;
