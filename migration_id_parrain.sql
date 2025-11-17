-- ============================================================================
-- MIGRATION : Correction du champ id_parrain dans parrainage
-- ============================================================================
-- 
-- Ce script corrige les champs id_parrain qui sont NULL dans la table parrainage.
-- 
-- IMPORTANT : 
-- 1. Exécutez ce script sur une copie de sauvegarde de votre base de données
-- 2. Pour l'ÉTAPE 1.2, vous devez déterminer quel parrain correspond à quel parrainage
--    selon votre logique métier (notifications, demandes de parrainage, etc.)
-- 3. PAS BESOIN de colonne id_parrain dans la table paiement
--    On récupère idParrain via la jointure : paiement → parrainage → id_parrain
-- ============================================================================

-- ============================================================================
-- ÉTAPE 1 : Corriger la table parrainage — remplir id_parrain
-- ============================================================================

-- Étape 1.1 : Identifier les parrainages qui ont des paiements mais pas d'id_parrain
SELECT 
    parr.id AS id_parrainage,
    parr.id_jeune,
    parr.id_formation,
    parr.id_parrain AS id_parrain_actuel,
    p.id AS id_paiement,
    COUNT(p.id) AS nombre_paiements
FROM parrainage parr
INNER JOIN paiement p ON p.id_parrainage = parr.id
WHERE parr.id_parrain IS NULL
GROUP BY parr.id, parr.id_jeune, parr.id_formation, parr.id_parrain
ORDER BY nombre_paiements DESC;

-- Étape 1.2 : Mettre à jour id_parrain dans parrainage
-- IMPORTANT : Vous devez déterminer quel parrain correspond à quel parrainage
-- en fonction de votre logique métier (notifications, demandes, etc.)
-- 
-- Exemple pour un parrainage spécifique (à adapter selon vos données) :
-- UPDATE parrainage parr
-- SET parr.id_parrain = [ID_DU_PARRAIN]  -- ← Remplacer par l'ID réel du parrain
-- WHERE parr.id = [ID_PARRAINAGE]  -- ← Remplacer par l'ID du parrainage
--   AND parr.id_parrain IS NULL;

-- OU si vous avez une autre table/source pour identifier le parrain :
-- UPDATE parrainage parr
-- SET parr.id_parrain = (
--     SELECT id_parrain FROM [autre_table] WHERE id_parrainage = parr.id
-- )
-- WHERE parr.id_parrain IS NULL;

-- ============================================================================
-- ÉTAPE 2 : Vérification - Récupérer idParrain via jointure (pas de colonne dans paiement)
-- ============================================================================

-- Vérifier que les paiements peuvent récupérer idParrain via parrainage
-- Cette requête simule ce que fait le backend Java
SELECT 
    p.id AS id_paiement,
    p.id_parrainage,
    parr.id_parrain AS idParrain,  -- ← Récupéré via jointure avec parrainage
    CASE 
        WHEN parr.id_parrain IS NOT NULL THEN 'OK - Parrain trouvé'
        WHEN p.id_parrainage IS NOT NULL THEN 'Parrainage sans parrain'
        ELSE 'Pas de parrainage (paiement direct)'
    END AS statut
FROM paiement p
LEFT JOIN parrainage parr ON parr.id = p.id_parrainage
ORDER BY p.date DESC
LIMIT 10;

-- ============================================================================
-- NOTE IMPORTANTE :
-- ============================================================================
-- - PAS BESOIN de colonne id_parrain dans la table paiement
-- - Récupérer idParrain via la jointure : paiement → parrainage → id_parrain
-- - Si paiement.id_parrainage est NULL → idParrain sera NULL (paiement direct)
-- - Si paiement.id_parrainage existe mais parrainage.id_parrain est NULL → idParrain sera NULL
-- - Si parrainage.id_parrain existe → idParrain contiendra l'ID du parrain
-- ============================================================================

