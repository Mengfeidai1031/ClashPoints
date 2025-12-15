# CLASHPOINTS - DOCUMENTACIÓN COMPLETA DEL PROYECTO

<img width="272" height="251" alt="image" src="https://github.com/user-attachments/assets/50335f18-bf15-40ca-800d-33bcab43d6c1" />

---

## DESCRIPCIÓN GENERAL

**ClashPoints** es un juego de trivia para Android que combina:
- Preguntas de cultura general en 6 categorías
- Sistema de ruleta animada para selección de categorías
- Sistema de puntuación tipo Kahoot (velocidad + precisión)
- Ranking global con Firebase Firestore
- UI moderna con Jetpack Compose y Material Design 3
### Características Principales:
- 90 preguntas (15 por categoría)
- 3 rondas de 4 preguntas cada una (12 preguntas totales)
- Temporizador de 25 segundos por pregunta
- Animación de ruleta de 4 segundos
- Ranking global Top 10
- Diseño con gradientes morados y rosados

---


## ARQUITECTURA DEL PROYECTO

El proyecto sigue la arquitectura **MVVM (Model-View-ViewModel)** con Jetpack Compose:

<img width="459" height="813" alt="image" src="https://github.com/user-attachments/assets/40549752-0009-47b6-a990-c8bce5e94ad3" />

--- 

## NAVEGACIÓN

**Flujo de navegación:**

```

Menu → NameInput → Roulette → Question → Result → Ranking
                        ↑          ↓
                        └──────────┘ (3 rondas)

```

**Características:**

- Usa `NavHost` de Jetpack Compose
- ViewModel compartido entre pantallas
- Gestión de back stack
- Navegación condicional basada en estado del juego

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
   │  → Animación de 4 segundos
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

### Flujo de Datos:

```

Usuario → UI (Compose) → ViewModel → Repository → Datos
                ↑                         ↓
                └─────── StateFlow ───────┘

```

---

## INTEGRACIÓN CON FIREBASE

### Configuración:

**Firebase Firestore**

<img width="579" height="199" alt="image" src="https://github.com/user-attachments/assets/76ada48f-befa-4089-b99b-955bddbf2543" />


### Mantenimiento del Top 10:

1. Cuando se guarda una puntuación nueva:
   
- Se obtiene el Top 10 actual
   
- Se verifica si la nueva puntuación entra
   
- Si entra, se guarda
   
- Se cuentan todas las entradas
   
- Si hay >10, se eliminan las peores

2. Esto asegura que siempre haya máximo 10 entradas.
---

## DISEÑO Y TEMA

### Paleta de Colores:

```
kotlin
ClashPointsPurple = #9D4EDD  // Morado principal
ClashPointsPink = #E91E8C    // Rosa principal
ClashPointsCyan = #00D9FF    // Cian (acento)
ClashPointsBackground = #0A0A0A  // Negro puro
ClashPointsSurface = #1A1A1A     // Gris muy oscuro
```

### Gradientes:

**Gradiente principal (botones):**

```
kotlin
Brush.horizontalGradient(
    colors = listOf(ClashPointsPurple, ClashPointsPink)
)
```

### Tipografía:

- **Títulos:** Bold, 22-32sp
- **Cuerpo:** Normal, 16-18sp
- **Etiquetas:** Medium, 11-14sp

### Espaciado:

- **Padding estándar:** 16dp, 24dp, 32dp
- **Bordes redondeados:** 12dp, 16dp, 20dp, 30dp
- **Tamaños de botón:** 250x60dp (grandes), 200x50dp (medianos)

---

## ESTADÍSTICAS DEL PROYECTO

### Pantallas Compose:
- 6 pantallas principales

### Preguntas:
- 90 preguntas totales
- 6 categorías
- 15 preguntas por categoría

### Dependencias:
- Jetpack Compose
- Firebase Firestore
- Navigation Compose
- Material Design 3
- Gson
- ViewModel

---

## RENDIMIENTO

### Optimizaciones:

1. **Carga de preguntas:**
- JSON cargado una vez al inicio   
- Almacenado en memoria

2. **Firebase:**   
- Persistencia offline habilitada  
- Listeners eficientes   
- Solo Top 10 en memoria

3. **UI:**   
- Compose recomposiciones optimizadas  
- StateFlow para reactive UI  
- Animaciones eficientes (60 FPS)

4. **Memoria:**   
- Pequeña huella de memoria   
- Sin memory leaks   
- Imágenes optimizadas

---

## TECNOLOGÍAS UTILIZADAS

### Lenguajes:
- **Kotlin** 1.9.20

### Frameworks:
- **Jetpack Compose** (UI moderna)
- **Material Design 3** (componentes)
- **Navigation Compose** (navegación)
- **ViewModel** (arquitectura)

### Backend:
- **Firebase Firestore** (ranking)

### Build System:
- **Gradle** 8.2 con Kotlin DSL

### Librerías:
- **Gson** (parsing JSON)
- **Coroutines** (asincronía)
- **StateFlow** (estado reactivo)
