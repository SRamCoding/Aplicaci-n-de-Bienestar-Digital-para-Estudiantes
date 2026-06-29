# 🌿 Equilibrio – App de Bienestar Digital para Estudiantes

Proyecto Final · Android · Kotlin · Firebase

---

## ⚡ Inicio rápido (3 pasos)

### Paso 1 — Ejecuta el script de configuración

**En Mac/Linux:**
```bash
cd Equilibrio
chmod +x setup.sh
./setup.sh
```

**En Windows:**
```
Haz doble clic en setup.bat
```

Esto descarga automáticamente el `gradle-wrapper.jar` necesario.

---

### Paso 2 — Configura Firebase

1. Ve a **https://console.firebase.google.com**
2. Crea un proyecto → nombre: `equilibrio-app`
3. Agrega app Android → package: `com.equilibrio`
4. Activa **Authentication → Email/Password**
5. Activa **Firestore Database**
6. Descarga `google-services.json`
7. **Reemplaza** el archivo `app/google-services.json` con el tuyo

**Reglas de Firestore** (pega en Firebase Console → Firestore → Reglas):
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      match /{sub=**} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
    match /tips/{tipId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null
        && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }
  }
}
```

---

### Paso 3 — Abrir en Android Studio

1. Abre Android Studio → **Open** → selecciona la carpeta `Equilibrio/`
2. Espera que Gradle sincronice (puede tomar 2-3 minutos la primera vez)
3. Conecta tu dispositivo Android o inicia un emulador (API 26+)
4. Presiona **Run ▶**

---

## 📱 Pantallas de la app

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

---

## 🔐 Crear usuario administrador

1. Ve a Firebase Console → Firestore → colección `users`
2. Abre el documento de tu usuario
3. Cambia el campo `role` de `"user"` a `"admin"`
4. El botón "Panel Admin" aparecerá en tu Perfil

---

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

---

## ⚠️ Nota sobre el permiso de uso de apps

El permiso `PACKAGE_USAGE_STATS` es especial y el usuario debe activarlo manualmente:
**Ajustes → Privacidad → Administrador de permisos → Uso de datos → Equilibrio → Permitir**

La app redirige automáticamente a esta pantalla al iniciar.
