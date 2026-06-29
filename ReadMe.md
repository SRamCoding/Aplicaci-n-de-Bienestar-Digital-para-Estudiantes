# Proyecto Final – Aplicación de Bienestar Digital para Estudiantes

## Descripción del Proyecto

El presente proyecto consiste en el desarrollo de una aplicación móvil enfocada en el bienestar digital y la salud mental de estudiantes universitarios. La aplicación permitirá a los usuarios monitorear el tiempo que pasan en redes sociales, establecer límites de uso, recibir recomendaciones personalizadas y acceder a herramientas de organización y relajación.

La aplicación busca ayudar a los estudiantes a mejorar su productividad académica y reducir los efectos negativos del uso excesivo de dispositivos móviles y redes sociales.

---

# Exposición del Problema

Actualmente, muchos estudiantes presentan problemas relacionados con el uso excesivo de redes sociales y dispositivos electrónicos. Esto puede generar distracción constante, disminución del rendimiento académico, ansiedad, estrés y alteraciones en los hábitos de sueño.

Además, existen pocas aplicaciones enfocadas específicamente en estudiantes universitarios que integren monitoreo digital, herramientas de productividad y apoyo al bienestar emocional en una sola plataforma.

Por esta razón, se propone desarrollar una aplicación accesible, intuitiva y funcional que ayude a los usuarios a administrar mejor su tiempo y mantener hábitos digitales saludables.

---

# Plataforma

La aplicación será desarrollada inicialmente para dispositivos Android utilizando las siguientes tecnologías:

- Android Studio
- Kotlin
- Firebase
- GitHub para control de versiones
- Figma para diseño de interfaces

En futuras versiones se podría considerar compatibilidad con iOS y versión web.

---

# Interfaz de Usuario e Interfaz de Administrador

## Interfaz de Usuario

La interfaz para los usuarios estará diseñada de manera simple, moderna e intuitiva. Algunas pantallas incluirán:

- Pantalla de inicio de sesión
- Registro de usuario
- Dashboard principal
- Estadísticas de uso de aplicaciones
- Recordatorios y alertas
- Configuración de límites diarios
- Consejos de bienestar y productividad

## Interfaz de Administrador

El administrador podrá:

- Gestionar usuarios
- Revisar estadísticas generales
- Publicar recomendaciones o contenido
- Supervisar reportes del sistema
- Gestionar notificaciones

---

# Funcionalidad

Las principales funcionalidades de la aplicación serán:

- Registro e inicio de sesión
- Monitoreo del tiempo de uso de aplicaciones
- Alertas por exceso de tiempo en redes sociales
- Estadísticas diarias y semanales
- Configuración de metas personales
- Consejos personalizados de productividad
- Recordatorios de descanso
- Sistema de notificaciones

---

# Diseño (Wireframes o Esquemas de Página)

## Pantalla de Inicio

- Logo de la aplicación
- Botón de iniciar sesión
- Botón de registro

## Dashboard Principal

- Tiempo total de uso diario
- Aplicaciones más utilizadas
- Gráficos estadísticos
- Recomendaciones personalizadas

## Pantalla de Configuración

- Límites de tiempo
- Preferencias de notificaciones
- Perfil del usuario

## Panel de Administrador

- Gestión de usuarios
- Publicación de contenido
- Estadísticas globales

---

# Objetivos del Proyecto

## Objetivo General

Desarrollar una aplicación móvil que ayude a los estudiantes a mejorar sus hábitos digitales y aumentar su productividad académica.

## Objetivos Específicos

- Monitorear el uso diario de aplicaciones móviles.
- Reducir el tiempo excesivo en redes sociales.
- Proporcionar herramientas de organización y bienestar.
- Crear una interfaz intuitiva y fácil de usar.

---

# Herramientas de Desarrollo

- Android Studio
- Kotlin
- Firebase
- GitHub
- Figma

---

# 📱 Pantallas de la app

| Pantalla | Descripción |
|---|---|
| Login | Correo/contraseña + recuperar contraseña |
| Registro | Nombre, universidad, carrera |
| Dashboard | Métricas, gráfico semanal, apps más usadas, metas |
| Estadísticas | Pastel por categoría, uso por hora, racha |
| Límites | Control +/− por app, bloqueo automático, modo estudio |
| Bienestar | Respiración 4-7-8 animada, Pomodoro con temporizador |
| Perfil | Logros, datos del usuario, cerrar sesión |
| Admin | Gestión de usuarios, publicar consejos |
| Notificaciones | Historial de alertas enviadas |

---

## 🏗️ Arquitectura

```
MVVM + Navigation Component + Firebase + Coroutines

ui/
├── auth/           → LoginFragment, RegisterFragment
├── dashboard/      → DashboardFragment, DashboardViewModel
│   └── adapter/   → AppUsageAdapter, GoalAdapter
├── stats/          → StatsFragment, StatsViewModel
├── limits/         → LimitsFragment
├── wellness/       → WellnessFragment, BreathingFragment, PomodoroFragment
├── profile/        → ProfileFragment
├── admin/          → AdminFragment, UserAdapter
└── notifications/  → NotificationsFragment, NotificationAdapter

data/
├── model/          → Models.kt (User, Goal, AppLimit, Notification, WellnessTip)
├── UsageMonitorService.kt  → Servicio en segundo plano
└── BootReceiver.kt         → Inicio automático al encender
```

## 📋 Permisos requeridos

| Permiso | Uso |
|---|---|
| `PACKAGE_USAGE_STATS` | Leer tiempo de uso de apps (activar manualmente en Ajustes del dispositivo) |
| `POST_NOTIFICATIONS` | Alertas al usuario |
| `INTERNET` | Firebase |
| `FOREGROUND_SERVICE` | Monitoreo en segundo plano |
| `RECEIVE_BOOT_COMPLETED` | Arranque automático |

---

## 🛠️ Tecnologías

- **Kotlin** + **Android Studio**
- **Firebase Auth** + **Firestore**
- **Navigation Component** (fragmentos)
- **MPAndroidChart** (gráficos)
- **Material Design 3**
- **ViewModel + LiveData** (MVVM)
- **Coroutines** (async)
- **UsageStatsManager** (monitoreo de apps)

