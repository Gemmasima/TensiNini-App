create database if not exists tensinini
	character set utf8mb4 -- para que soporte caracteres especiales (acentos y ñ)
    collate utf8mb4_unicode_ci; -- para comparaciones con caracteres
    
 use tensinini;   
 create table usuarios (
	id int auto_increment primary key,
    nombre varchar(100) not null,
    email varchar (150) not null unique, -- unique para evitar duplicados
    password varchar (200) not null,  -- irá hasheada
    creado_en datetime default current_timestamp -- (fecha+hora autom. default) al insertar un valor
    ) engine=InnoDB;
    
    create table mediciones (
		id int auto_increment primary key,
        paciente_id int not null,
        sistolica int not null, 
        diastolica int not null, 
        pulso int not null, 
        franja enum ('Mañana', 'Tarde', 'Noche') not  null,
        estado_emocional enum ('Tranuila', 'Nerviosa', 'Enfadada') not null, 
        fecha_hora datetime not null,
        foreign key (paciente_id) references usuarios(id)
        ) engine=InnoDB;