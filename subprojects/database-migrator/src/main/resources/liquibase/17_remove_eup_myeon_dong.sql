--liquibase formatted sql
--changeset swann:17
DELETE FROM eup_myeon_dong WHERE id IN (
  '8CPQl44ILSKWP+LDJ6fFzTd5k/LDMNg9r/Bz'
);
