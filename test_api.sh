#!/bin/bash
# Script de pruebas para API REST con JWT
# Uso: bash test_api.sh

BASE_URL="http://localhost:8082"
ADMIN_USER="admin"
ADMIN_PASS="admin123"

# Colores para terminal
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   Script de Pruebas - API JWT${NC}"
echo -e "${BLUE}================================================${NC}\n"

# 1. Test de conexión básica
echo -e "${YELLOW}[1/6] Probando conexión básica...${NC}"
RESPONSE=$(curl -s -X GET "${BASE_URL}/api/auth/test")
echo -e "${GREEN}Response:${NC} $RESPONSE\n"

# 2. Login
echo -e "${YELLOW}[2/6] Ejecutando Login...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"${ADMIN_USER}\",\"password\":\"${ADMIN_PASS}\"}")

echo -e "${GREEN}Response:${NC}"
echo "$LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$LOGIN_RESPONSE"

# Extraer token
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.refreshToken' 2>/dev/null)

if [ "$JWT_TOKEN" = "null" ] || [ -z "$JWT_TOKEN" ]; then
    echo -e "${RED}❌ Error: No se pudo obtener el token${NC}\n"
    exit 1
fi

echo -e "${GREEN}✓ Token obtenido exitosamente${NC}\n"

# 3. Obtener usuario actual
echo -e "${YELLOW}[3/6] Obteniendo usuario actual (/api/auth/me)...${NC}"
ME_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/auth/me" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json")

echo -e "${GREEN}Response:${NC}"
echo "$ME_RESPONSE" | jq '.' 2>/dev/null || echo "$ME_RESPONSE"
echo ""

# 4. Listar usuarios (requiere ADMIN)
echo -e "${YELLOW}[4/6] Listando todos los usuarios (/api/auth/users)...${NC}"
USERS_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/auth/users" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json")

echo -e "${GREEN}Response:${NC}"
echo "$USERS_RESPONSE" | jq '.' 2>/dev/null || echo "$USERS_RESPONSE"
echo ""

# 5. Refresh Token
echo -e "${YELLOW}[5/6] Renovando token (/api/auth/refresh-token)...${NC}"
REFRESH_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/refresh-token" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"${REFRESH_TOKEN}\"}")

echo -e "${GREEN}Response:${NC}"
echo "$REFRESH_RESPONSE" | jq '.' 2>/dev/null || echo "$REFRESH_RESPONSE"
echo ""

# 6. Prueba de seguridad - Acceso sin token
echo -e "${YELLOW}[6/6] Prueba de protección - acceso sin token...${NC}"
PROTECTED_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/auth/me" \
  -H "Content-Type: application/json")

echo -e "${GREEN}Response (debería ser acceso denegado):${NC}"
echo "$PROTECTED_RESPONSE" | jq '.' 2>/dev/null || echo "$PROTECTED_RESPONSE"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${GREEN}✓ Pruebas completadas${NC}"
echo -e "${BLUE}================================================${NC}"
