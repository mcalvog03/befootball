PGDMP  3                    }           final    17.4    17.2 B    5           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            6           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            7           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            8           1262    24576    final    DATABASE     k   CREATE DATABASE final WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'es-ES';
    DROP DATABASE final;
                     postgres    false                        3079    40960    pgcrypto 	   EXTENSION     <   CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
    DROP EXTENSION pgcrypto;
                        false            9           0    0    EXTENSION pgcrypto    COMMENT     <   COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';
                             false    2                       1255    73728    actualizar_clasificacion()    FUNCTION       CREATE FUNCTION public.actualizar_clasificacion() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ 
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

        -- Calcular la diferencia de goles (permitiendo valores negativos)
        dif_goles_local := NEW.goles_local - NEW.goles_visitante;
        dif_goles_visitante := NEW.goles_visitante - NEW.goles_local;

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
$$;
 1   DROP FUNCTION public.actualizar_clasificacion();
       public               postgres    false            �            1259    73733 "   clasificacion_pk_clasificacion_seq    SEQUENCE     �   CREATE SEQUENCE public.clasificacion_pk_clasificacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 9   DROP SEQUENCE public.clasificacion_pk_clasificacion_seq;
       public               postgres    false            �            1259    24740    clasificacion    TABLE     j  CREATE TABLE public.clasificacion (
    pk_clasificacion bigint DEFAULT nextval('public.clasificacion_pk_clasificacion_seq'::regclass) NOT NULL,
    liga_id bigint,
    equipo_id bigint,
    puntos integer,
    partidos_jugados integer,
    partidos_ganados integer,
    partidos_empatados integer,
    partidos_perdidos integer,
    diferencia_goles integer
);
 !   DROP TABLE public.clasificacion;
       public         heap r       postgres    false    233            �            1259    24653    equipos    TABLE     �   CREATE TABLE public.equipos (
    pk_equipo bigint NOT NULL,
    nombre character varying,
    liga_id bigint,
    estadio_id bigint,
    escudo character varying
);
    DROP TABLE public.equipos;
       public         heap r       postgres    false            �            1259    24738    equipos_pkEquipo_seq    SEQUENCE     �   ALTER TABLE public.equipos ALTER COLUMN pk_equipo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."equipos_pkEquipo_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    220            �            1259    24721    estadios    TABLE     _   CREATE TABLE public.estadios (
    pk_estadio bigint NOT NULL,
    nombre character varying
);
    DROP TABLE public.estadios;
       public         heap r       postgres    false            �            1259    24739    estadios_pkEstadio_seq    SEQUENCE     �   ALTER TABLE public.estadios ALTER COLUMN pk_estadio ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."estadios_pkEstadio_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    224            �            1259    24677 	   jugadores    TABLE     �   CREATE TABLE public.jugadores (
    pk_jugador bigint NOT NULL,
    nombre character varying,
    equipo_id bigint,
    nacionalidad bigint,
    posicion character varying,
    dorsal integer
);
    DROP TABLE public.jugadores;
       public         heap r       postgres    false            �            1259    24735    jugadores_pkJugador_seq    SEQUENCE     �   ALTER TABLE public.jugadores ALTER COLUMN pk_jugador ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."jugadores_pkJugador_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    222            �            1259    24640    ligas    TABLE     �   CREATE TABLE public.ligas (
    pk_liga bigint NOT NULL,
    nombre character varying,
    temporada character varying,
    pais_id bigint
);
    DROP TABLE public.ligas;
       public         heap r       postgres    false            �            1259    24734    ligas_pkLiga_seq    SEQUENCE     �   ALTER TABLE public.ligas ALTER COLUMN pk_liga ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."ligas_pkLiga_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    219            �            1259    24660    paises    TABLE     Z   CREATE TABLE public.paises (
    pk_pais bigint NOT NULL,
    nombre character varying
);
    DROP TABLE public.paises;
       public         heap r       postgres    false            �            1259    24733    paises_pkPais_seq    SEQUENCE     �   ALTER TABLE public.paises ALTER COLUMN pk_pais ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."paises_pkPais_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    221            �            1259    24694    partidos    TABLE       CREATE TABLE public.partidos (
    pk_partido bigint NOT NULL,
    equipo_local bigint,
    equipo_visitante bigint,
    goles_local integer,
    goles_visitante integer,
    estado character varying,
    jornada integer,
    liga_id bigint,
    fecha timestamp with time zone
);
    DROP TABLE public.partidos;
       public         heap r       postgres    false            �            1259    24736    partidos_pkPartido_seq    SEQUENCE     �   ALTER TABLE public.partidos ALTER COLUMN pk_partido ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."partidos_pkPartido_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    223            �            1259    90112    roles    TABLE     V   CREATE TABLE public.roles (
    pk_rol integer NOT NULL,
    rol character varying
);
    DROP TABLE public.roles;
       public         heap r       postgres    false            �            1259    90124    roles_pk_rol_seq    SEQUENCE     �   ALTER TABLE public.roles ALTER COLUMN pk_rol ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.roles_pk_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    234            �            1259    24577    usuarios    TABLE       CREATE TABLE public.usuarios (
    pk_usuario bigint NOT NULL,
    nombre character varying,
    correo character varying,
    equipo_favorito bigint,
    salt character varying,
    password_hash character varying,
    fecha_registro timestamp with time zone,
    rol_id integer
);
    DROP TABLE public.usuarios;
       public         heap r       postgres    false            �            1259    24737    usuarios_pkUsuario_seq    SEQUENCE     �   ALTER TABLE public.usuarios ALTER COLUMN pk_usuario ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."usuarios_pkUsuario_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    218            /          0    24740    clasificacion 
   TABLE DATA           �   COPY public.clasificacion (pk_clasificacion, liga_id, equipo_id, puntos, partidos_jugados, partidos_ganados, partidos_empatados, partidos_perdidos, diferencia_goles) FROM stdin;
    public               postgres    false    232   �c       #          0    24653    equipos 
   TABLE DATA           Q   COPY public.equipos (pk_equipo, nombre, liga_id, estadio_id, escudo) FROM stdin;
    public               postgres    false    220   f       '          0    24721    estadios 
   TABLE DATA           6   COPY public.estadios (pk_estadio, nombre) FROM stdin;
    public               postgres    false    224   �l       %          0    24677 	   jugadores 
   TABLE DATA           b   COPY public.jugadores (pk_jugador, nombre, equipo_id, nacionalidad, posicion, dorsal) FROM stdin;
    public               postgres    false    222   r       "          0    24640    ligas 
   TABLE DATA           D   COPY public.ligas (pk_liga, nombre, temporada, pais_id) FROM stdin;
    public               postgres    false    219   ��       $          0    24660    paises 
   TABLE DATA           1   COPY public.paises (pk_pais, nombre) FROM stdin;
    public               postgres    false    221   �       &          0    24694    partidos 
   TABLE DATA           �   COPY public.partidos (pk_partido, equipo_local, equipo_visitante, goles_local, goles_visitante, estado, jornada, liga_id, fecha) FROM stdin;
    public               postgres    false    223   Ϗ       1          0    90112    roles 
   TABLE DATA           ,   COPY public.roles (pk_rol, rol) FROM stdin;
    public               postgres    false    234   C�       !          0    24577    usuarios 
   TABLE DATA           |   COPY public.usuarios (pk_usuario, nombre, correo, equipo_favorito, salt, password_hash, fecha_registro, rol_id) FROM stdin;
    public               postgres    false    218   z�       :           0    0 "   clasificacion_pk_clasificacion_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('public.clasificacion_pk_clasificacion_seq', 419, true);
          public               postgres    false    233            ;           0    0    equipos_pkEquipo_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public."equipos_pkEquipo_seq"', 138, true);
          public               postgres    false    230            <           0    0    estadios_pkEstadio_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public."estadios_pkEstadio_seq"', 117, true);
          public               postgres    false    231            =           0    0    jugadores_pkJugador_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('public."jugadores_pkJugador_seq"', 499, true);
          public               postgres    false    227            >           0    0    ligas_pkLiga_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public."ligas_pkLiga_seq"', 6, true);
          public               postgres    false    226            ?           0    0    paises_pkPais_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public."paises_pkPais_seq"', 83, true);
          public               postgres    false    225            @           0    0    partidos_pkPartido_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public."partidos_pkPartido_seq"', 152, true);
          public               postgres    false    228            A           0    0    roles_pk_rol_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.roles_pk_rol_seq', 2, true);
          public               postgres    false    235            B           0    0    usuarios_pkUsuario_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public."usuarios_pkUsuario_seq"', 8, true);
          public               postgres    false    229            u           2606    24659    equipos equipos_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.equipos
    ADD CONSTRAINT equipos_pkey PRIMARY KEY (pk_equipo);
 >   ALTER TABLE ONLY public.equipos DROP CONSTRAINT equipos_pkey;
       public                 postgres    false    220            �           2606    24744    clasificacion pkClasificacion 
   CONSTRAINT     k   ALTER TABLE ONLY public.clasificacion
    ADD CONSTRAINT "pkClasificacion" PRIMARY KEY (pk_clasificacion);
 I   ALTER TABLE ONLY public.clasificacion DROP CONSTRAINT "pkClasificacion";
       public                 postgres    false    232            }           2606    24727    estadios pk_estadio 
   CONSTRAINT     Y   ALTER TABLE ONLY public.estadios
    ADD CONSTRAINT pk_estadio PRIMARY KEY (pk_estadio);
 =   ALTER TABLE ONLY public.estadios DROP CONSTRAINT pk_estadio;
       public                 postgres    false    224            y           2606    24683    jugadores pk_jugador 
   CONSTRAINT     Z   ALTER TABLE ONLY public.jugadores
    ADD CONSTRAINT pk_jugador PRIMARY KEY (pk_jugador);
 >   ALTER TABLE ONLY public.jugadores DROP CONSTRAINT pk_jugador;
       public                 postgres    false    222            s           2606    24646    ligas pk_liga 
   CONSTRAINT     P   ALTER TABLE ONLY public.ligas
    ADD CONSTRAINT pk_liga PRIMARY KEY (pk_liga);
 7   ALTER TABLE ONLY public.ligas DROP CONSTRAINT pk_liga;
       public                 postgres    false    219            w           2606    24666    paises pk_pais 
   CONSTRAINT     Q   ALTER TABLE ONLY public.paises
    ADD CONSTRAINT pk_pais PRIMARY KEY (pk_pais);
 8   ALTER TABLE ONLY public.paises DROP CONSTRAINT pk_pais;
       public                 postgres    false    221            {           2606    24700    partidos pk_partido 
   CONSTRAINT     Y   ALTER TABLE ONLY public.partidos
    ADD CONSTRAINT pk_partido PRIMARY KEY (pk_partido);
 =   ALTER TABLE ONLY public.partidos DROP CONSTRAINT pk_partido;
       public                 postgres    false    223            �           2606    90118    roles pk_rol 
   CONSTRAINT     N   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT pk_rol PRIMARY KEY (pk_rol);
 6   ALTER TABLE ONLY public.roles DROP CONSTRAINT pk_rol;
       public                 postgres    false    234            q           2606    24588    usuarios pk_usuario 
   CONSTRAINT     Y   ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT pk_usuario PRIMARY KEY (pk_usuario);
 =   ALTER TABLE ONLY public.usuarios DROP CONSTRAINT pk_usuario;
       public                 postgres    false    218            ~           1259    73731    idx_clasificacion_liga_equipo    INDEX     l   CREATE UNIQUE INDEX idx_clasificacion_liga_equipo ON public.clasificacion USING btree (liga_id, equipo_id);
 1   DROP INDEX public.idx_clasificacion_liga_equipo;
       public                 postgres    false    232    232            �           2620    81920 )   partidos trigger_actualizar_clasificacion    TRIGGER     �   CREATE TRIGGER trigger_actualizar_clasificacion AFTER INSERT OR UPDATE ON public.partidos FOR EACH ROW EXECUTE FUNCTION public.actualizar_clasificacion();
 B   DROP TRIGGER trigger_actualizar_clasificacion ON public.partidos;
       public               postgres    false    283    223            �           2606    24684    jugadores fk_equipo    FK CONSTRAINT     }   ALTER TABLE ONLY public.jugadores
    ADD CONSTRAINT fk_equipo FOREIGN KEY (equipo_id) REFERENCES public.equipos(pk_equipo);
 =   ALTER TABLE ONLY public.jugadores DROP CONSTRAINT fk_equipo;
       public               postgres    false    222    4725    220            �           2606    24716    usuarios fk_equipo    FK CONSTRAINT     �   ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT fk_equipo FOREIGN KEY (equipo_favorito) REFERENCES public.equipos(pk_equipo) NOT VALID;
 <   ALTER TABLE ONLY public.usuarios DROP CONSTRAINT fk_equipo;
       public               postgres    false    218    4725    220            �           2606    24745    clasificacion fk_equipo    FK CONSTRAINT     �   ALTER TABLE ONLY public.clasificacion
    ADD CONSTRAINT fk_equipo FOREIGN KEY (equipo_id) REFERENCES public.equipos(pk_equipo);
 A   ALTER TABLE ONLY public.clasificacion DROP CONSTRAINT fk_equipo;
       public               postgres    false    4725    232    220            �           2606    24706    partidos fk_equipo_local    FK CONSTRAINT     �   ALTER TABLE ONLY public.partidos
    ADD CONSTRAINT fk_equipo_local FOREIGN KEY (equipo_local) REFERENCES public.equipos(pk_equipo);
 B   ALTER TABLE ONLY public.partidos DROP CONSTRAINT fk_equipo_local;
       public               postgres    false    220    4725    223            �           2606    24711    partidos fk_equipo_visitante    FK CONSTRAINT     �   ALTER TABLE ONLY public.partidos
    ADD CONSTRAINT fk_equipo_visitante FOREIGN KEY (equipo_visitante) REFERENCES public.equipos(pk_equipo);
 F   ALTER TABLE ONLY public.partidos DROP CONSTRAINT fk_equipo_visitante;
       public               postgres    false    223    4725    220            �           2606    24728    equipos fk_estadio    FK CONSTRAINT     �   ALTER TABLE ONLY public.equipos
    ADD CONSTRAINT fk_estadio FOREIGN KEY (estadio_id) REFERENCES public.estadios(pk_estadio) NOT VALID;
 <   ALTER TABLE ONLY public.equipos DROP CONSTRAINT fk_estadio;
       public               postgres    false    220    4733    224            �           2606    24672    equipos fk_liga    FK CONSTRAINT     }   ALTER TABLE ONLY public.equipos
    ADD CONSTRAINT fk_liga FOREIGN KEY (liga_id) REFERENCES public.ligas(pk_liga) NOT VALID;
 9   ALTER TABLE ONLY public.equipos DROP CONSTRAINT fk_liga;
       public               postgres    false    4723    219    220            �           2606    24750    clasificacion fk_liga    FK CONSTRAINT     y   ALTER TABLE ONLY public.clasificacion
    ADD CONSTRAINT fk_liga FOREIGN KEY (liga_id) REFERENCES public.ligas(pk_liga);
 ?   ALTER TABLE ONLY public.clasificacion DROP CONSTRAINT fk_liga;
       public               postgres    false    4723    219    232            �           2606    32794    partidos fk_liga    FK CONSTRAINT     ~   ALTER TABLE ONLY public.partidos
    ADD CONSTRAINT fk_liga FOREIGN KEY (liga_id) REFERENCES public.ligas(pk_liga) NOT VALID;
 :   ALTER TABLE ONLY public.partidos DROP CONSTRAINT fk_liga;
       public               postgres    false    219    223    4723            �           2606    24667    ligas fk_pais    FK CONSTRAINT     |   ALTER TABLE ONLY public.ligas
    ADD CONSTRAINT fk_pais FOREIGN KEY (pais_id) REFERENCES public.paises(pk_pais) NOT VALID;
 7   ALTER TABLE ONLY public.ligas DROP CONSTRAINT fk_pais;
       public               postgres    false    221    219    4727            �           2606    24689    jugadores fk_pais    FK CONSTRAINT     {   ALTER TABLE ONLY public.jugadores
    ADD CONSTRAINT fk_pais FOREIGN KEY (nacionalidad) REFERENCES public.paises(pk_pais);
 ;   ALTER TABLE ONLY public.jugadores DROP CONSTRAINT fk_pais;
       public               postgres    false    222    4727    221            �           2606    90119    usuarios fk_rol    FK CONSTRAINT     {   ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT fk_rol FOREIGN KEY (rol_id) REFERENCES public.roles(pk_rol) NOT VALID;
 9   ALTER TABLE ONLY public.usuarios DROP CONSTRAINT fk_rol;
       public               postgres    false    218    234    4738            /   �  x�]�m��0E�{�'c�{����qo:RrU�J���bO?׼�_֟����5 �@�0:`	\�[` ��Ї�4�p
���Q���(`�b0r1�s��d���f���P�� �̆!�Y��P�q��Y�3$�B�!q����pv0h���@W4�����;��wܼ�o�X�&�\�c�Y�ީ7D���۸���=lH�6v6{���?��\�G:�1Q���;��)#�?�d���!�~:U����W�~ kk�MyZ�����.̷��B�Xn��g>�7Äŏ�i�����)B��\�c�W�l��\&�"[c>������a��{��|������������=r��&[�������8k|�r�[��E����ոKQPI��$��6�n_Hn�@4Q.�(N��L��"�|��HP��y��/���Q�Q���(`Tb0*1
�1�%F�#�L��mS�r��TT������ �LR낀�YWTԺ$��Vt��΋nr�y�M�;/�Ʌ�E7���&W�����\z^tsq+����Q��m<�E��~P�؋T$�d�>�@E�{)��})���	T$� �C�� *n�n&n�n&n�&�&���������>����DP      #   �  x�U��r��c�)m�����$-J�ky]�LW���$�A����Kz�-�6�r�/vO�A{o�(P=��Fw�:1_-��O�食ƃ�D�A7'��g���A;eao3(�s1F���X���Pb3�ڍ;�DfP"����_�?k9�O�;r+��V��A�AǅK(�+��è�|���5aRµ�=�0�r5��XM���dn�dnů������ϴP��x�M��Q�4�3�b�g���N�Sb�J$����{�ˡ�g���5%��Aɣo��^���r(��P��~��#&��k�G	%�J�i/�����H���OkG��q8M�<j(y4�#���p���J��E�i}�Q��:��B�'�V�/_��O���|<X=�^.���Lp���|��Am���C.�B�^[����0Qڎd��N�=cSrQ��wzT;S#8�Vܙ��aˣ	��*����ĵP{�R���m�և��Il��?5Lqm���M-�{�����^9O�����FQ/����bR1���5���ުk�aÐާ���ĸk}�Ε�%�b�	<�]ob �����!`YڶbB���f5m&P�j�=�p�[���v̀)��@�b+Z���|sDJ�	���ek^�ZAl�a���)�^Clo�k}�a4g/�(��a��}�}Zqk�T c���mI���`�j7�r��̥
��c4C�r��L���6�Il.����>ZQ�uQ������ᏂC.*�`�Ȗ�������Ū}\��،�V=�\�P��İ��5^;�x1�T�8��<����G��/�R���(��Z��G�V�����ѯ�j"�.��3BY���y��L=p�gĳT{kT0�	�Ռh�K�����&�V2Y�������[=DL���Q�����E�)��������/_ȡC�g�N��w*���S+�h�._�?3AtYjc�=*sd��x!��=�w�FU��#e)xT���q�F�g)v����yjUb>K��|W��Yk���7��2�m:�M��8�c�z�o)}�k��Q��1
�3U}{DZ͕!;�9W�(�!������a��1.��?�^!> d��1�8(�L��:6**�����ƒ_P��#>,��FIb{A�w����Q�m�F%�=��|1�]8�:X���oDY�<y�֣�t�4�~C����̝|��k�a���0 e�����mɡ�ء�递�Lq{a�ȸP�&�qc�}�+����Fr� >�2����9���MM`+ޠ*��bj'7��a�	��a�gq-�9��=�R��VAz�b��q4N֐�-���Mb}�;�n�{A���6b�[��8��^��`�8GU����0��Z�I	.���%��{��I���`��Ts6�<����ou|�0֜�z�������4��8��ck�4,͙�K�;ֈ#]��|m�>�ȅ�.��e�^)�"Ĥq�f\��p-���E�%�Z�������+3�NeՄ)i1�@��t���%���N&�=]�Ҩ��wGM8��p������B�<�� �#NT3Ô�������ܐr	�p��"�|����
�
i�5�-]���ļ"����(qa�?�������'-1Aj�I����cZ ��_a'��p��5-jr#6�"����6�;Z(z�y��ի�Cۀ�      '   8  x�uVKr�6]���I�����x���r쪩lzH�B
�c�r��R�ŋYM�,��Mr�<�Cj��%Ѝ�ׯ��i�)WV�R̤���:g��c� #fT6�O�����]Y�<��lJ�J|�5�YmT�V���c��|�mŇ�zzr*�I]���u%�RL��5�����5�H��4��+�O��;���!��G�Vt\{r�גG�U��@k�2[��T���x�ڼ��̨�Q�K���Jk*�5_������}j^k��X�b�.mռ��`0��ј]���F,��-��h�ֿ�8�(e3	�A/���$��5/�� �Zє��\��Hǭ貖D���P��txo��q��k�n�����)$`2�
;��ۡ$���C-���~
�y<z��"���ݪv�?0��qF�������,Oz?��˕��$ϓ��Ȇ��ē��7W dڼ�R��2B&:���<���*T�ߕA���F2x;����ಣ�ĵ��>���P1	�H(�d|�eA.Wfv�c�o��N�~�{W(����
6g�1���~�ʅ����!�6GA5�j���{���Z�G'��٦_�A���@��F�5�\�⒊�옞w�h��1k����*���kCbN����غ��m/R̂��n�������������"va5<*s��1���	��9�^���g�'r�'�y�
=����u�C6w�#�߿���d.�#��}@�1�A�)p_/]���Z?TOT�����J�:È�N��]xC���G3��f-�!�X"�S�A�d{�hj'+%���`�u[/Cvq�n�0b?��r���ٙS~}tGHd�0ewJ燖F�@I��%M{�q�%��y�쒖fJr6곫�ۓl�vB���H�b�z4|����-�nc�[�񎲥���
#�?��c�H�yYuwa`�nKp�s/��u�~9|��I)?w�M��֡����ǘ�R/k�vgn�έ�U�>�^`�9zD-L��d1�[�K,�kK9��2���'t��Y���G�s����ZR�	cd37���&@&.�D��)�p����@>r��玆t�n�H�%����jUx�+kr�ތ�f�Ⱥ#$Mٹ�+�Z���a�� M��ٜ��X�%"0�f3ذp{1��U���6l/A`)��ꦼ
�s�W�24B���7�<�Տqzc��$�a�8/K�9�$[#�v"u&�
�R��2Gs�ǘAa�n6�D���N��'Uu�EQH&>jV���ݶD��p�}��N���el�a�F�]�b>Q�Ė5���%.d09d'
\��P5yX�����p���-\����?�>�      %      x�}\K��Jz]S��`�-.��~�m���fS��R�(V_R�ki�U�e ��YyaAp1;e���s��D��Y�tT���8竏�9�|�s��{�$�xN�|��V��F��Z���K��o:��睾F�3]��-w���8�XVrO�x�Й�2s��_E�/}�F~�y)̷ܹ�Ȝh9�"�*��0�_�Â��G�H��n�(̌�p���Qr��5�v�
���y.�U���Q��K3��p��wn��4�B��X�{��K�;�3��X�L3�.E�@�EV/z��p�1d^����|ev.r�
$2W���,��6��XU�ď2�L�E�Y��
ǐ���&��OZ��;����=��L�s]�z�[�LF��kQd�t?���>��cBF���J>��jn�Bi�:��!��Z� j��{xLb#Kv���� �{#o�QD����4�`�i;]����=��-�Fw�ыF����7�o�{�s�F�3�h��G���I�a�7l�����1f�ڇ7�x#����]0|	�^b|�e"S�Bڱ���7j-k��!.����D�^���ey��b珹Pp���g��}O��U&sw�TE!�+}L0�'΍�*?�'%��4�a�j���(����Q0v>�$w��3v̆�F�W������_/�`��|���W����� �n��
��F�WZ�x��(i�<��Х�ɬ>�R:
"���
.�V�+i���Q;�-�	IS����FA����3��,��A4�珂�sg[��X4��A9mx-�B.�h Gḝ�]����<,`+��hz�{��+"P����j����(��{q��i�MFa���W�̑?�]�OGa��&�ݛ�~^��Q�88��`�������p¸����ʎF���-�>pz�0�;=S9� ��� �[I���K�<����S~g����1p����>���2��[���ȹF
��M��*S�K,�;����I��Z�7ģh�f�[��\����d��E�9F�`�������������Uf��'��w����N�;��L&(��'�tq�Ko��Ǖ�@���� 	�ǆ|�r��oK	�(��ӿS����o�qc͗�Zé�];O��x�PRzN
��HLV(bM�2RF3m"g`�� $���*�SJ�a�yc3��a2J�V���^�OB��0W��{���e���0� D��ID�L���¡�����Ճ�0C�GI���MC�^���{C���-�L�g������5m�)W۠�h2&�{�4�l!�F�&���#���Ĩ�I���F�o� �����s&����ހ-�kg@�/��0<%
��$��EX͹��,2Mpv�l>���B�DP|��,���=�|l6�� ��s8�ZI&�%OmrN�¸5�_����0���=��A��,k�f'�"ؓT��<S0�'�x�p���x��aW�2�\IFr���`L��
Co��
�]q���y�Q:�y��l������ƣ�k��GF��PD��w���J�+��5Af�×��\l�ò�4�Vl>��d=`'�����6�ٕVK��ɂi�7l���\�:XbP���������x�0����N��C� 2h�W�_Զ�L�PJ�t�zb�44��c�l7M��σ���Ý7�u'�/2��kߢ/���ّ����b�FӃ.��g��,7�t�Y#
��#�����ܪ9��)5�Zz��Do������U"˴�si�s/�%��5ɣ*�F@RClΫQ'2DwWD'��Р'��i�W8���%�B���{����*�@~�	�\�P3��hI�s(���R@�i�z���&��em08v��9�B]����[vyl��+�s\�19�jU8����q�;b�6�8�jM$�BwG<V9�  u�t� �W ��ec��^�C8|�jʹ|����6��T�3��
��k�ߦ��B�s&2�i>h���Cph~�.f͊�.$ �$EQ��7�fV!< Bi;,�@2�x-2�[����V�T:�`-��e|�F�9͖I�	��m0�`�F�;�igh�}���U��G3��xJ+��~��>�ܗ�u
.Ƿz�d����������>�=����
`�cj�D&�o`1(��y7�E!����KAr��tYA-Q0��;0� 8L�<[T�����1!F�:��`�����Ql�Ň�T�^���;� ]ۧ��B�� M܅U���QS�`(sa�a�A��z��If��i���ֿ�,��r�P���C,��fڥ7��G�?�0=p=cz���k��]�x!�8�4�F������v�m{��T����q�j)�I_�{�F��^��{AB�S&�2O0F�{ږ�	0XsG�x	�B58	��v U0ur���0ѝ%牭��������,��n�q�'Ĩ�{}��2�1�&Cŏ�>J�9��ZR
�����A���!����UGPM, '�=-��
���y�I�0��s�C�g&�U�94���Y���RK�����!W���}"�\I����0�Y!�����{�1 �(jHd��&�=�!�!�f����+]�O�U��8A<�1�̌m��+�����PmD=�=pĬ���(x�6\Vl/�y�8�Ԛ��c�[�|��%($����k@�/LH26�d�߸E�[�1`9	D��Ăcc��b���`!,�)�D��&6��%�s������~�&���!ŀ��QX������I�`ҋ��E���^c��d�!�vwb���V�{�Ե���fp�����n�������.<�B���+����;�x�F��ziő	�ASO�%�ǳP���w��!�.�V#�-І�Ԗ*gOҘ�<��Z=!$��
�[A�o��x�0�,i��c���n����ߧ��q�iZg�����݇~{��52��H=��^���q-6��U��& �8�:{��L�� �xc��F���7��a}��Lk���ȹ�9؇�?ZK��m��yk����P߷y�����mfn���f3s��s�9_UE�dk�Cv�C���٭7S뵔� 5%,L=v qT�jÛ��/���&��kϤ��}�T�n�
����_t��׺XJ���|_b��8kt�I�Y���3�\AX��V����y�����'M��1���% ��l�`�]���؆`�O�`�����J���ȼ,�v�V��g�i�\=�'ؗ�l��;ͶjCR��|h�;���G�ټqq>6t>3���;]���������Wj>7��CWɓ�-t5��%����dh��@�E.Zk8�t�ښ�I��a|ɯ9��H{�v��b��Y�.�jwl���jMDh-bQج�*W�|�/��,��ÈA�M��N�D�����aK
A�2�hU߇�/�+����K}���" n�F�j)'���P �����d����מ����D��:|7�����'��Ü���!I�/����m]#����Bq'?X �By���=o��}�J �i���n?���M�~0�-	�U��QҔnŗ/Ҍ:���%����u1��?������;�����C�}�|#�n�Ef�y���nP���[���4��uZ�\����K�OGpj�������M���x�V�X���q�8E��/����۞��Gl���� ��,\��'��o�q0�~��bו͞��)���\�ͪP�*cޡ�0�`�׻L��
e�)�peFY^ ���9��ek�C_�o�`+μ�����
�.弽�ߐ�+���F}[rL����\X���U�I�Q��0�\ر��$i:ڈeo]�I�`�
	ӣeڛH�RIڍ�v;k�m�ݴ���#�9v�9w���=o�i�@�|�;o��(�m��g�۵�\&A]n(t�[��$4�[�:���Κ��Y�$j�ʫ�����8�@1�/��'ϓV�Ib	�Q�UwR�V˨��\kzl�ș��4Au�'�Rh�f�{�`)��o�M� m  �� �6��U� 52�=������ٹ!��ZB7��z�M
�����6���j�/�����~^e0�#B��7��@�=��v��Y�`K
Ɍ�zE?�օcMYCR��ᵡ�Q q�A>���Hn6�޷�^��W_����l��L���_��������r??4h8���F��'�wMw��j3Us �>��t'WSmӾ8�|I lZ�Z1l�	 �x�y�}�@b@p2b�"�1&䬐s���+�r-��N�d�ޘ�B1^l���=�' {�{�ڏ�|���BD �?���p��&:h8���겏�v�A����p,�Lر�w�W�r?�L�ys\^sk�ē�i�``�� �U�7fY��^�"��񺨂	)��K`X���ף��T[&�n
�Yt�;��g��V�o�y�ÛvC鏍W��u�CM�	(7ـ��&�I�6��"&�jAdlS��h�;0]��#e!����	��qW������ ��������8�̵M��l=����}���I+ވ��}�{���}TL��O�X�6e2��	�NQ,�zO���m���}��� O"3ɩS5�\@�^(��d��Y��~��A �W��IƟw���EF- ���!���Gx��D6Du	�"H�is�wPW21����{��u�)��8Ѵ%W}ñEx{8�5ŮvK�Lz�u�?z�*���;t:�����O*��ۚ������3Wb-6��؁�'�A�퍰�j%V[�J�.|���s�w��*gv��7W�{$��?���Vej{�Om��u����o��v���=���6�w���BD�4��i�����<�T.�#IF��L�A�5���س�{+�ƈ�:4�	���r��&��R	�9�e�~o*�5��t��1h 6$�:$��*�3%s�#>���U=鎗G�G�/�*�-x¼$A^pF�H�g��K�#C��pgd ��mŻ��΍uLΥx �_sH�p�KRＩ�N��e�����Jl�C��1�3��O*WsU��k�.j˳ze웾��R�A��P,$�߲�4K-�Bg�dK�"kq�CFΛs�ۙx|d1���r�1�+wq�:а2��`^��ɍ2���r �o(ޭ��?��
��N���3`o��Le^!;���i� V,�ڛW��3b�����h���`�۽�,��0�ۆ�2��֏��8�&�1���-��Б�柪�̏���%�KGӂ񳨈�dgZ,�KxG�@E�.x�1��n+��o��{��&fku�dS�Y:w_m�Ts��7Ù!���8>VE�ȧ�sv@����t���E�u�W�E�����(�R�[5�F��W�{�0�h����v0S.����+A}���%�,���7<�q�����~���Z�$q�W�pĶ+9_���n�W.&���
�����3pG���x���lM2���y���N�e��ć]rA���4�Z%��9؂H�;o�x�R2W'�V�����׼{�^]�h	Pڍ i�<�Uׇ���b��C�s�F��*��=@���J>�ܩZhn��B@�J�Z�k��o�����0["D�҃����l�85�k��}Λ��ۆ�q�t���K�|)�����	�c�He���tb�n�,��[>��.�>���6I�m9_�gؾW�A��l�@�371-G��M�ҧ��t�>�â�����<D�c�ŀд�f�r�kC�f�.]A��	�j�V�#/9l8nۭ6��KWU�����7�D0_�Ϧ����*�<�ܟd-�� ����c1ە�m'���м�Z�&h��
��N�,�_!o��{a.fD)Z{=�wN�]�Е{��jòy;���	�]�ƫ�[�B��O����V���^�m�ݎ� H�N	��[}�/
�~��~+���X3���a�~�B��h�.��5_V;ث���b�C9\�� �"��R�Y��'�u_��5��W�V��m,Ql�k`��	ɻ2���j��ө@��܈��o"|<�u*�!_�5�#��͌�{��P�����\�A�7�{�Aڽ$#D$�7�w��V�N��;�*hnή*��!�.&
!��ZT��Ϊ��&$cS�$�em��+6���n
��C��flk,6�!�v�Z���Z�S���0@<��fI6/e$����>�/օA�*���=Ӟ��/�����)�� C*C���L��� �
R_�]�N�����יtoP�4�m�aq]?��E���e��|Q����C��:�nu�������0r>�B��sKL�~o�
�'�q����7$�允���3���E�8��[#F8i�����zPkz�6'	}������;E���f�-�?�<M@�v�;��,,`��X<�)��u��Ǚ9�mWaĦ�G�U�I��E��l���<�s'FQ����iS��wGA@���U�J�9�e%-=�f���.�¨}M�tV����I�|��	Z�|�:״�N�f��@`� &v�Y^�b
������WqP�tZ�f�17�a��7�)b��a9l[�5�m�I�Jb� �[U\�a,N�U\CC���%~�&������(���Ƿ!㴾�=��q���Df�%�M��o����>��g�x�A����χ�%7�!���uI��m5�0i//������"�ҟ���v���Q7���8��x�G!��p��n��k�+�,�L6�"D����o�i��'|e��ʝ9��g<��7��g����7����N	�o�m�z���M����m��9�㹄���WB�hOǐ���2����w������y�)����z�i�3�^y�9��|%r��1{Nl��/��r�uYn��R�����:��Z�=���\l�������a߾��+.}�����F��Z��      "   r   x�3��I��LOTpuT.�/*)�4202����\F0i�ʂԢ�����<ƜN�y)��9@EH\&H
F(R��E����E
>��饩H��\f���E��
�H��\1z\\\ {�*�      $   �  x�-�=r�@�k�<�G��Tʊ��d��XV*7��6�"$��I|��.T�uˋ+��,���=��R:׏�'��Vh�-��>8,`=}��c	wÑ���|�OX÷��sXw�^�<�����fO���m{�2������&���SOk�������.�o4���tFS���N�M�ҁ�S���԰��#M���-�L��$�Fqf����ii��7��s�>B�S��5��a�O�a^�F����[�+x��X+�q���V{���/��;F;SWBk�9�)NgB�ÊN���F���m�F.
���2�����u!�DA[k&�kS�G��9��)���l8�Eڵ�쇓V�h��t��[�y�7Ƣ��x=\Qk�H-�{X�0��S,4�NX�4j)ģ����oYD�u밺������� �?�ڻ�      &   d  x���ˎ�0E��Wd?�@$���t3hQ @1���{)Oc˖�:g6�	)^��RH�ē������������71	�����]��a
er��ӛ�K�������������¸+�I���I �'����$���n!u�9%eR9�yR�g�.��9�B^���i�ŉ�$��s�ؑ�{�Ɯ�/z�sJ�)��9�<���(�9�H!Q��ِCf�@�g�q���u�<i���ߜ ,����!X"�WN����G�y�zF���KD!�KyX��S�n����[�tb���B�ث���d<��d�l��
E�oFC��|�m}C?]&W����X5k�^��-&R��Q�ql��wr�y��L�Pd�1#{�Q���a���e�)Q�a<�M� ���n��p+�L 斌�ȵHYA�@�ZPG�Z>-V�j��tc$����\�����|��#DVV�\m�e�fL'��B��Sd=��:b#z���[	7�Z�\E�O�=kM�ɏ�J��y=�ʍ�9X�@ti��"+��,zٶ�)��yN�]�o���9��-�+@]�p���}�2O�e`ܦU��w̡Ts�@Z��H��u/0k���n�%��X{4�#d�N�̚b5#ee�
��|;�uA�ER�|k��D�Gώxz���*"!ho>A�翰�x������(���[K���l޼,iJ�|p�f=ZW�2ޡ�'l���B�$�����=dL"I���MdL����5��������g����ny���t�Qf��[�B�51r�=A�6����x5�6{y�E"����gm$�aB*Y���d�4��Ä��݃��ic�U"O�u�0��1D�٪�lF�X�2��v�\.T�      1   '   x�3�tqv
���qt��2�u������� {'(      !   �  x�U�K��@��u�+��NUBb"�c{E���R(�Q@�5�����L&9ɷ|��0T^��Gf$!:@3{��?��*�}��73����H�GXi��Ϯ�f諾O;���X~��;��i5�U��L�>�}`]����cʴ�S��9���IQ�<�s�P�_%~=��$�uv��ңV�m�q}�\43Py��'�O��%�_{_�0�!���5���WBhT���$�@U�UB:���\�B�}��D��h"/���f�r2�5e��	�n:Ξ����K�����bj�o���r<Y��g�f}�t�
5@�0#��z�����(jĉGi!F� �DY�'�:_��玵ݍ�ƚ������}��v��b5 ���麶���	������0�} V������t��B�k     