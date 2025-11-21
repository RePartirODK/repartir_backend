-- ============================================================================
-- MIGRATION : Conversion du statut de paiement de INTEGER vers STRING
-- ============================================================================
-- 
-- Ce script migre les données existantes de la colonne 'status' dans la table 'paiement'
-- de INTEGER (0, 1, 2, etc.) vers STRING ('EN_ATTENTE', 'VALIDE', 'REFUSE', etc.)
--
-- IMPORTANT : Exécutez ce script UNIQUEMENT si vous avez des données existantes
--             avec des statuts stockés comme entiers.
-- ============================================================================

-- Étape 1 : Vérifier les valeurs actuelles (optionnel, pour diagnostic)
SELECT status, COUNT(*) as nombre 
FROM paiement 
GROUP BY status;

-- Étape 2 : Créer une colonne temporaire pour la migration
ALTER TABLE paiement 
ADD COLUMN status_temp VARCHAR(50);

-- Étape 3 : Convertir les valeurs INTEGER vers STRING
-- Mapping :
-- 0 ou NULL -> 'EN_ATTENTE'
-- 1 -> 'VALIDE'
-- 2 -> 'REFUSE'
-- 3 -> 'A_REMBOURSE'
-- 4 -> 'REMBOURSE'

UPDATE paiement 
SET status_temp = CASE 
    WHEN status = 0 OR status IS NULL THEN 'EN_ATTENTE'
    WHEN status = 1 THEN 'VALIDE'
    WHEN status = 2 THEN 'REFUSE'
    WHEN status = 3 THEN 'A_REMBOURSE'
    WHEN status = 4 THEN 'REMBOURSE'
    ELSE 'EN_ATTENTE'
END;

-- Étape 4 : Supprimer l'ancienne colonne
ALTER TABLE paiement 
DROP COLUMN status;

-- Étape 5 : Renommer la colonne temporaire
ALTER TABLE paiement 
CHANGE COLUMN status_temp status VARCHAR(50);

-- Étape 6 : Vérifier le résultat
SELECT status, COUNT(*) as nombre 
FROM paiement 
GROUP BY status;

-- ============================================================================
-- NOTE : Hibernate créera automatiquement la colonne avec le bon type
--        lors du prochain redémarrage grâce à @Enumerated(EnumType.STRING)
-- ============================================================================






