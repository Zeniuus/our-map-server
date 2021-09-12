--liquibase formatted sql
--changeset swann:1
CREATE TABLE `si_gun_gu` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sido` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO si_gun_gu VALUES
("4dfclKdNhc7SEHjmFJ5ApUByGlUqNcfgxvQm", "성남시 수정구", "경기도"),
("Z8+ciJ+l861XqbGd5tN0xiqiq8zp4PKmYyHP", "성남시 분당구", "경기도"),
("4eFRTLDzHaUwRHOriGv+RA6TAp8EKorKqghP", "성남시 중원구", "경기도");

CREATE TABLE `eup_myeon_dong` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO eup_myeon_dong VALUES
("dWQO9O1BEYuEt40JLF4AElIUa5NrqH0eLP1U", "신흥동"),
("/PBZcqccAKoeXWupuMCjgnTdeLhcJlytnNoe", "태평동"),
("Lmc6R5ZlmFdPUVSJ6C8+Sd+UUpdPhyBoDeAW", "수진동"),
("rLhxBbouknzwykwBGWnYIqNLulTG3LMWa/JP", "단대동"),
("fI3M5XtcFjBvsKp266NUjGag7SSGeOCyDj/3", "산성동"),
("tPXzu7klIpTACx7CQBw5DTTb5lVXnxMeUqHF", "양지동"),
("iDn9cX01se+w2rf0vZ7gYwjQC+5g/hyeIbSk", "복정동"),
("i6sOrp0mSll/sNPn+ehoT4xQkWZVUnuRAAFJ", "창곡동"),
("OVeOMEpkaBp0/n+AP1f/RAWZ3jAMWzRjTqbt", "신촌동"),
("g4ZLJzkEY692EE9W8AWfnnULu2bRxMXSHuxg", "오야동"),
("+BpENvtXXXq9io1xThqT4zrxlupWo32g9np6", "심곡동"),
("0F/OLFoPWJf2f+cgym0CZk8JIAf91ZWm2FTL", "고등동"),
("0FD3WitK0ktGKNGnbfCbfutBGgZIJLaEXlu7", "상적동"),
("8CPQl44ILSKWP+LDJ6fFzTd5k/LDMNg9r/Bz", "둔전동"),
("47y59zBlwy+NT7nisG2EQsIgs8N+2tviZ4PJ", "시흥동"),
("V4+PZ3YDyAiXsqJl5JHH5WtHiMPUl8UZiMsd", "금토동"),
("cW/dd3vxvmOYJa1IfcURm7yz5c6Vx0/8u0H0", "사송동"),
("dugAKR/7Fb5OtidkRrJ5GCfpQ4rsVf9pOblf", "분당동"),
("lHHObsjuiQoE/8AtNAwqvnZI7TNtwlI4pPeV", "수내동"),
("xbXrqN5gzHmmfohVyE73gdmMLsyBCAzbAV3O", "정자동"),
("cHlx3zIYH+YcPbHU1IO9SBcVdfyUpVVUZ8Zc", "서현동"),
("1e7oKRtN86Xkq+iQEFiJ+fZHktrnbr6x6Am5", "율동"),
("Kv4WoY+c36CZJyUdtYE+H7FBKOVnB98qAl0M", "이매동"),
("n+N58MSZkpBv6fJdMW+O+aQylHTEkiPclzb+", "야탑동"),
("XRBzoD//CJwimRXb1TfJh8bhb9J0PPSeD4RP", "금곡동"),
("R4p8TP9XeLNwjk66aK9a2/wKNhwCjNL6JZSs", "궁내동"),
("pWiNdiNgUlLp5DYNBCnT86behT1d3Dm5qXMi", "구미동"),
("Bwg17EpFsFF/d1deDoy2bISOmnwAQWCjdavS", "동원동"),
("XkrMgrwEFBh9YT4/C/u7Zf5kaU1JZoyArU3F", "판교동"),
("EUeBJkOUnkZxclJyrSxxy7tQaRUkOexr+3XE", "삼평동"),
("Z/1ZAs6iYVUZAWBWRrZeAf4xow/P8167UH4Z", "백현동"),
("nYKEC6dFsP60qa9a+b9O6zCH/iaMLqv0ABVu", "운중동"),
("bwuv60WMybdRE7HS8KgyHcPXAJANT+6mMLvd", "대장동"),
("j1osKqqkCWBoEajUElW+tSUquflWqi2AY5m+", "석운동"),
("iDg5Fx4ElNomLw/IA1PAC266/Ymk49nO/h3t", "하산운동"),
("JLrp7HOvaK/ItUYoj78keUgI2KeZULeXYEDO", "성남동"),
("wdyyIiHLq8bIpfI5E3F3tiHBaO8BmNRX6lvm", "중앙동"),
("jJsJBM4RyBFnxLPG1rluxRLHNtjC88bRrOS8", "금광동"),
("NkVrSB4kNbndWbutmekt08UgazdLBjwGqDqR", "은행동"),
("H/GMuz/rEsxmdE72M/Orm9BF5X1Xr3/BMESu", "상대원동"),
("HKhT+udgFZvKuTmRwroSqsK68o62bsLQlwru", "하대원동"),
("VUT8E/W2mvrxCeDallbDND5yfVyK5Mk9UVVh", "갈현동"),
("nBFkB5TjCBPQpsZL5mo6J/lOb6dt3iZHFEtj", "도촌동"),
("e4pSC7Q7gmeqHRTBl8Qg1w+LQ0k5wCxH7in7", "여수동");
