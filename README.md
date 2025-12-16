# CLASHPOINTS

<img width="272" height="251" alt="image" src="https://github.com/user-attachments/assets/50335f18-bf15-40ca-800d-33bcab43d6c1" />

---

## DESCRIPCIÓN GENERAL
**ClashPoints** es un juego de trivia para Android que combina preguntas de cultura general, una ruleta animada para selección de categorías, y un sistema de puntuación basado en velocidad y precisión.

### Características

- **12 preguntas** distribuidas en 3 rondas de 4 preguntas cada una
- **6 categorías**: Historia, Deportes, Arte, Geografía, Entretenimiento y Ciencia
- **Sistema de puntuación**: a mayor velocidad de respuesta, más puntos (máximo 1000 puntos por pregunta)
- **Temporizador de 25 segundos** por pregunta con indicador visual
- **Ruleta animada** para seleccionar la categoría de cada ronda
- **Ranking global** con los mejores 10 jugadores guardado en Firebase
- **Autenticación persistente**: tu nombre se guarda automáticamente para futuras partidas
- **UI moderna** con Jetpack Compose y Material Design 3

---

## ARQUITECTURA DEL PROYECTO

El proyecto sigue la arquitectura **MVVM (Model-View-ViewModel)** con Jetpack Compose:

<img width="459" height="813" alt="image" src="https://github.com/user-attachments/assets/40549752-0009-47b6-a990-c8bce5e94ad3" />

```

Usuario → UI (Compose) → ViewModel → Repository → Datos
                ↑                         ↓
                └─────── StateFlow ───────┘

```

---

## FLUJO DE LA APLICACIÓN

### Flujo Completo del Juego:

```

1. INICIO
   ↓
2. MenuScreen
   → Usuario presiona "Start"
   ↓
3. NameInputScreen
   → Usuario ingresa nombre
   → Validación (mínimo 2 caracteres)
   ↓
4. BUCLE DE JUEGO (3 rondas)
   ├─ RouletteScreen
   │  → Animación de 3 segundos
   │  → Selección de categoría aleatoria
   │  ↓
   ├─ QuestionScreen (4 preguntas)
   │  → Pregunta 1 (25 segundos)
   │  → Pregunta 2 (25 segundos)
   │  → Pregunta 3 (25 segundos)
   │  → Pregunta 4 (25 segundos)
   │  ↓
   └─ Repetir para Ronda 2 y 3
   ↓
5. ResultScreen
   → Muestra puntuación final
   → ¿Guardar en ranking?
   ├─ Sí → Guardar en Firebase → RankingScreen
   └─ No → MenuScreen
   ↓
6. RankingScreen
   → Muestra Top 10
   → Botón volver → MenuScreen

```

---

## INTEGRACIÓN CON FIREBASE

<img width="579" height="199" alt="image" src="https://github.com/user-attachments/assets/76ada48f-befa-4089-b99b-955bddbf2543" />

### Firestore
- **Colección `questions`**: 90 preguntas (15 por categoría)
- **Colección `scores`**: Ranking global (top 10)

### Authentication
- **Autenticación anónima**: permite guardar el nombre del jugador
- **DisplayName persistente**: el nombre se rellena automáticamente en futuras partidas
- **Sin necesidad de registro**: juega inmediatamente

--- 

## TECNOLOGÍAS UTILIZADAS

- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI declarativa
- **Material Design 3** - Diseño y componentes
- **Navigation Compose** - Navegación entre pantallas
- **Firebase Firestore** - Base de datos en tiempo real
- **Firebase Authentication** - Gestión de usuarios
- **Coroutines & StateFlow** - Programación asíncrona y estado reactivo
- **GitHub Actions** - CI/CD para builds y tests automáticos

## Autores
- [Mario García Abellán](https://github.com/Mario-Grc)
- [Meng Fei Dai](https://github.com/Mengfeidai1031)
