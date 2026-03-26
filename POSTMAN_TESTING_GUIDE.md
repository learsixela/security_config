# 📡 GUÍA COMPLETA - PRUEBAS DE API CON POSTMAN

## 🚀 Preparación en Postman

### 1. Crear una colección nueva
1. Abrir Postman
2. Click en **"New"** → **"Collection"**
3. Nombre: `JWT Security API`
4. Click en **"Create"**

### 2. Crear ambiente (Environment)
1. Click en **"Environments"** (lado izquierdo)
2. Click en **"New"**
3. Nombre: `Local Development`
4. Agrega estas variables:

```
BASE_URL = http://localhost:8082
TOKEN = (se llenará después del login)
REFRESH_TOKEN = (se llenará después del login)
```

5. Click en **"Save"**

---

## 🧪 PRUEBAS PASO A PASO

### ✅ PASO 1: Test de Conexión Básica

**Request:**
```
GET http://localhost:8082/api/auth/test
```

**En Postman:**
1. Click en **"+"** para nueva pestaña
2. Cambiar a **GET**
3. URL: `{{BASE_URL}}/api/auth/test`
4. Click en **Send**

**Respuesta esperada (200 OK):**
```json
{
  "message": "API REST funcionando correctamente",
  "timestamp": "2026-03-25 10:30:45"
}
```

---

### ✅ PASO 2: Login y Obtener Tokens

**Request:**
```
POST http://localhost:8082/api/auth/login
```

**En Postman:**
1. Nueva pestaña
2. Cambiar a **POST**
3. URL: `{{BASE_URL}}/api/auth/login`
4. Ir a pestaña **Body** → **raw** → seleccionar **JSON**
5. Pegar:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

6. Click en **Send**

**Respuesta esperada (200 OK):**
```json
{
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjoiW1JPTEVfQURNSU5dIiwiaWF0IjoxNzExMzU2MjQ1LCJleHAiOjE3MTE0NDI2NDV9.abc123...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzExMzU2MjQ1LCJleHAiOjE3MTE5NjEwNDV9.xyz789...",
  "user": {
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

### 🔑 Guardar Tokens en Postman

1. En la respuesta anterior, **selecciona y copia el valor de `token`**
2. Click en el **ojo** (Environment Quick Look) lado derecho
3. Click en **Local Development**
4. En la variable `TOKEN`, pegá el token
5. **Haz lo mismo con `REFRESH_TOKEN`**

Ahora puedes usar `{{TOKEN}}` en los headers de las próximas llamadas.

---

### ✅ PASO 3: Obtener Usuario Actual (Protegido)

**Request:**
```
GET http://localhost:8082/api/auth/me
```

**En Postman:**
1. Nueva pestaña
2. Cambiar a **GET**
3. URL: `{{BASE_URL}}/api/auth/me`
4. Ir a **Headers** y agregar:

```
Key: Authorization
Value: Bearer {{TOKEN}}
```

5. Click en **Send**

**Respuesta esperada (200 OK):**
```json
{
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**Si falla (401 Unauthorized):**
- Verifica que el TOKEN está en el environment
- Verifica que copiaste correctamente el token del login
- Verifica que el formato es `Bearer <token>` (con espacio)

---

### ✅ PASO 4: Listar Todos los Usuarios (Solo ADMIN)

**Request:**
```
GET http://localhost:8082/api/auth/users
```

**En Postman:**
1. Nueva pestaña
2. Cambiar a **GET**
3. URL: `{{BASE_URL}}/api/auth/users`
4. **Headers:**
```
Key: Authorization
Value: Bearer {{TOKEN}}
```

5. Click en **Send**

**Respuesta esperada (200 OK):**
```json
[
  {
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  },
  {
    "username": "user1",
    "email": "user1@example.com",
    "role": "USER"
  }
]
```

**Si falta (403 Forbidden):**
- El usuario no tiene rol ADMIN
- Registra otro usuario con ese rol en BD

---

### ✅ PASO 5: Registrar Nuevo Usuario

**Request:**
```
POST http://localhost:8082/api/auth/register
```

**En Postman:**
1. Nueva pestaña
2. Cambiar a **POST**
3. URL: `{{BASE_URL}}/api/auth/register`
4. **Body** → **raw** → **JSON**

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123"
}
```

5. Click en **Send**

**Respuesta esperada (201 Created):**
```json
{
  "message": "Usuario registrado exitosamente",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "username": "newuser",
    "email": "newuser@example.com",
    "role": "USER"
  }
}
```

---

### ✅ PASO 6: Renovar Token (Refresh)

**Request:**
```
POST http://localhost:8082/api/auth/refresh-token
```

**En Postman:**
1. Nueva pestaña
2. Cambiar a **POST**
3. URL: `{{BASE_URL}}/api/auth/refresh-token`
4. **Body** → **raw** → **JSON**

```json
{
  "refreshToken": "{{REFRESH_TOKEN}}"
}
```

5. Click en **Send**

**Respuesta esperada (200 OK):**
```json
{
  "message": "Token renovado exitosamente",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Guardar nuevo token:**
- Copia el nuevo `token`
- Actualiza la variable `{{TOKEN}}` en el environment

---

## 🔐 Pruebas de Seguridad

### ❌ Test 1: Sin Token

**Request:**
```
GET http://localhost:8082/api/auth/me
```

**Sin header de Authorization**

**Respuesta esperada (403 Forbidden):**
```
Access Denied o No token provided
```

---

### ❌ Test 2: Token Inválido

**Request:**
```
GET http://localhost:8082/api/auth/me
```

**Headers:**
```
Authorization: Bearer invalid_token_here
```

**Respuesta esperada (403 Forbidden):**
```
Invalid or expired token
```

---

### ❌ Test 3: Token Expirado

Espera 24 horas (o modifica el config a menor tiempo para test) y luego intenta usar un token viejo.

**Respuesta esperada (401 Unauthorized):**
```json
{
  "message": "Token inválido o expirado"
}
```

---

### ❌ Test 4: Sin Permisos (Usuario Normal vs ADMIN)

1. Login como usuario normal
2. Intenta acceder a `GET {{BASE_URL}}/api/auth/users`
3. Usa el token de usuario normal en Authorization header

**Respuesta esperada (403 Forbidden):**
```
Access Denied - Insufficient permissions
```

---

## 📋 Colección Postman Completa (JSON)

Puedes importar esto directamente en Postman:

1. Click en **Import**
2. Selecciona **Raw text**
3. Pega esto:

```json
{
  "info": {
    "name": "JWT Security API",
    "description": "Colección completa de pruebas JWT",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Test Conexión",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{BASE_URL}}/api/auth/test",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "test"]
        }
      }
    },
    {
      "name": "2. Login",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"admin\",\"password\":\"admin123\"}"
        },
        "url": {
          "raw": "{{BASE_URL}}/api/auth/login",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "login"]
        }
      }
    },
    {
      "name": "3. Obtener Usuario Actual",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{TOKEN}}"
          }
        ],
        "url": {
          "raw": "{{BASE_URL}}/api/auth/me",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "me"]
        }
      }
    },
    {
      "name": "4. Listar Usuarios",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{TOKEN}}"
          }
        ],
        "url": {
          "raw": "{{BASE_URL}}/api/auth/users",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "users"]
        }
      }
    },
    {
      "name": "5. Registrar Usuario",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"newuser\",\"email\":\"new@example.com\",\"password\":\"password123\"}"
        },
        "url": {
          "raw": "{{BASE_URL}}/api/auth/register",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "register"]
        }
      }
    },
    {
      "name": "6. Renovar Token",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"refreshToken\":\"{{REFRESH_TOKEN}}\"}"
        },
        "url": {
          "raw": "{{BASE_URL}}/api/auth/refresh-token",
          "host": ["{{BASE_URL}}"],
          "path": ["api", "auth", "refresh-token"]
        }
      }
    }
  ]
}
```

---

## 📊 Resumen de Endpoints

| Endpoint | Método | Auth | Descripción |
|----------|--------|------|-------------|
| `/api/auth/test` | GET | No | Test conexión |
| `/api/auth/login` | POST | No | Login (recibe user/pass) |
| `/api/auth/register` | POST | No | Registrar usuario |
| `/api/auth/me` | GET | Sí | Datos usuario actual |
| `/api/auth/users` | GET | Sí (ADMIN) | Listar todos |
| `/api/auth/refresh-token` | POST | No | Renovar JWT |

---

## ⚠️ Headers Importantes

### Para endpoints protegidos:
```
Authorization: Bearer <tu_token_jwt>
Content-Type: application/json
```

### Para registrar/login:
```
Content-Type: application/json
```

---

## 🐛 Troubleshooting

### "Unauthorized (401)"
- Verifica que incluiste el header `Authorization`
- Verifica que el formato es `Bearer <token>` (con espacio)
- Verifica que el token no expiró

### "Forbidden (403)"
- No tienes permisos suficientes
- Verifica el rol del usuario (ADMIN vs USER)

### "Bad Request (400)"
- El JSON no está bien formado
- Faltan campos requeridos en el body
- El email ya existe (en registro)

### "Internal Server Error (500)"
- Revisar los logs de la aplicación
- Verifica que la BD está disponible
- Verifica que los datos son válidos

---

## 💡 Tips Útiles

1. **Guardar respuestas:** En Postman, las respuestas se guardan automáticamente en el historio → **History**

2. **Variables dinámicas:** Usa scripts en la pestaña **Tests** para guardar valores automáticamente:
```javascript
var jsonData = pm.response.json();
pm.environment.set("TOKEN", jsonData.token);
pm.environment.set("REFRESH_TOKEN", jsonData.refreshToken);
```

3. **Pre-request Script:** Para agregar headers automáticamente en pestaña **Pre-request Script**:
```javascript
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('TOKEN')
});
```

4. **Tests automáticos:** En pestaña **Tests**, puedes validar respuestas:
```javascript
pm.test("Status is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Token existe", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.exist;
});
```

---

## 🚀 Flujo Completo Recomendado

1. **Test de conexión** ✅
2. **Login** ✅ (guardar TOKEN)
3. **Obtener usuario** ✅
4. **Listar usuarios** ✅
5. **Registrar nuevo usuario** ✅
6. **Renovar token** ✅
7. **Pruebas de seguridad** ✅

---

**Listo para probar con Postman! 📡**
