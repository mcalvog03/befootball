-- Crear la función del Trigger
CREATE OR REPLACE FUNCTION actualizar_clasificacion() RETURNS TRIGGER AS $$ 
DECLARE 
    puntos_local INT; 
    puntos_visitante INT; 
    dif_goles_local INT; 
    dif_goles_visitante INT; 
BEGIN 
    -- Si se está insertando un nuevo partido o actualizando un resultado 
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN 
        
        -- Validar que los goles no sean negativos
        IF NEW.goles_local < 0 OR NEW.goles_visitante < 0 THEN
            RAISE EXCEPTION 'Los goles no pueden ser negativos';
        END IF;

        -- Determinar puntos y diferencia de goles
        IF NEW.goles_local > NEW.goles_visitante THEN
            puntos_local := 3;
            puntos_visitante := 0;
        ELSIF NEW.goles_local < NEW.goles_visitante THEN
            puntos_local := 0;
            puntos_visitante := 3;
        ELSE
            puntos_local := 1;
            puntos_visitante := 1;
        END IF;

        -- Calcular la diferencia de goles
        dif_goles_local := NEW.goles_local - NEW.goles_visitante;
        dif_goles_visitante := NEW.goles_visitante - NEW.goles_local;

        -- Asegurarse de que las diferencias de goles no sean negativas
        IF dif_goles_local < 0 THEN 
            dif_goles_local := 0; 
        END IF;
        
        IF dif_goles_visitante < 0 THEN 
            dif_goles_visitante := 0; 
        END IF;

        -- Si es una actualización, revertimos los datos anteriores
        IF TG_OP = 'UPDATE' THEN
            UPDATE clasificacion
            SET puntos = puntos - (CASE 
                                      WHEN OLD.goles_local > OLD.goles_visitante THEN 3
                                      WHEN OLD.goles_local < OLD.goles_visitante THEN 0
                                      ELSE 1 
                                  END),
                partidos_ganados = partidos_ganados - (CASE WHEN OLD.goles_local > OLD.goles_visitante THEN 1 ELSE 0 END),
                partidos_empatados = partidos_empatados - (CASE WHEN OLD.goles_local = OLD.goles_visitante THEN 1 ELSE 0 END),
                partidos_perdidos = partidos_perdidos - (CASE WHEN OLD.goles_local < OLD.goles_visitante THEN 1 ELSE 0 END),
                diferencia_goles = diferencia_goles - (OLD.goles_local - OLD.goles_visitante),
                partidos_jugados = partidos_jugados - 1
            WHERE equipo_id = OLD.equipo_local AND liga_id = OLD.liga_id;

            UPDATE clasificacion
            SET puntos = puntos - (CASE 
                                      WHEN OLD.goles_visitante > OLD.goles_local THEN 3
                                      WHEN OLD.goles_visitante < OLD.goles_local THEN 0
                                      ELSE 1 
                                  END),
                partidos_ganados = partidos_ganados - (CASE WHEN OLD.goles_visitante > OLD.goles_local THEN 1 ELSE 0 END),
                partidos_empatados = partidos_empatados - (CASE WHEN OLD.goles_visitante = OLD.goles_local THEN 1 ELSE 0 END),
                partidos_perdidos = partidos_perdidos - (CASE WHEN OLD.goles_visitante < OLD.goles_local THEN 1 ELSE 0 END),
                diferencia_goles = diferencia_goles - (OLD.goles_visitante - OLD.goles_local),
                partidos_jugados = partidos_jugados - 1
            WHERE equipo_id = OLD.equipo_visitante AND liga_id = OLD.liga_id;
        END IF;

        -- Actualizar la clasificación del equipo local
        INSERT INTO clasificacion (liga_id, equipo_id, puntos, partidos_ganados, partidos_empatados, partidos_perdidos, diferencia_goles, partidos_jugados)
        VALUES (NEW.liga_id, NEW.equipo_local, puntos_local, 
                CASE WHEN puntos_local = 3 THEN 1 ELSE 0 END, 
                CASE WHEN puntos_local = 1 THEN 1 ELSE 0 END, 
                CASE WHEN puntos_local = 0 THEN 1 ELSE 0 END, 
                dif_goles_local, 1)
        ON CONFLICT (liga_id, equipo_id) 
        DO UPDATE SET 
            puntos = clasificacion.puntos + EXCLUDED.puntos,
            partidos_ganados = clasificacion.partidos_ganados + EXCLUDED.partidos_ganados,
            partidos_empatados = clasificacion.partidos_empatados + EXCLUDED.partidos_empatados,
            partidos_perdidos = clasificacion.partidos_perdidos + EXCLUDED.partidos_perdidos,
            diferencia_goles = clasificacion.diferencia_goles + EXCLUDED.diferencia_goles,
            partidos_jugados = clasificacion.partidos_jugados + EXCLUDED.partidos_jugados;

        -- Actualizar la clasificación del equipo visitante
        INSERT INTO clasificacion (liga_id, equipo_id, puntos, partidos_ganados, partidos_empatados, partidos_perdidos, diferencia_goles, partidos_jugados)
        VALUES (NEW.liga_id, NEW.equipo_visitante, puntos_visitante, 
                CASE WHEN puntos_visitante = 3 THEN 1 ELSE 0 END, 
                CASE WHEN puntos_visitante = 1 THEN 1 ELSE 0 END, 
                CASE WHEN puntos_visitante = 0 THEN 1 ELSE 0 END, 
                dif_goles_visitante, 1)
        ON CONFLICT (liga_id, equipo_id) 
        DO UPDATE SET 
            puntos = clasificacion.puntos + EXCLUDED.puntos,
            partidos_ganados = clasificacion.partidos_ganados + EXCLUDED.partidos_ganados,
            partidos_empatados = clasificacion.partidos_empatados + EXCLUDED.partidos_empatados,
            partidos_perdidos = clasificacion.partidos_perdidos + EXCLUDED.partidos_perdidos,
            diferencia_goles = clasificacion.diferencia_goles + EXCLUDED.diferencia_goles,
            partidos_jugados = clasificacion.partidos_jugados + EXCLUDED.partidos_jugados;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear el Trigger
CREATE TRIGGER trigger_actualizar_clasificacion
AFTER INSERT OR UPDATE ON partidos
FOR EACH ROW EXECUTE FUNCTION actualizar_clasificacion();
