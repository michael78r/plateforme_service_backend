# Plateforme de vente — Backend (Spring Boot)

API REST d'une boutique e-commerce : catalogue, commandes, paiements, factures, authentification JWT et chat temps réel.

## 🧱 Stack
- **Java 21** · **Spring Boot 4**
- **Spring Data JPA / Hibernate** · **PostgreSQL**
- **Spring Security** + **JWT** (jjwt) avec refresh tokens
- **WebSocket / STOMP** (chat) · **Bean Validation**
- Build : **Maven** (`./mvnw`)

## 📂 Architecture (par feature / domaine)
```
com.example.restservice
├── user/        Utilisateur, Specialite, RoleType, RefreshToken
│                + AuthService, TokenService, AuthController, UtilisateurController
├── catalog/     Category, Product (+ repos, ProductService, ProductController, dto/)
├── order/       Order, OrderItem, OrderStatus (+ service, controller, dto/)
├── payment/     Payment, PaymentStatus, PaymentMethod (+ service, controller, dto/)
├── invoice/     Invoice, InvoiceStatus (+ service)
├── client/      Client (module CRM, indépendant de la vente)
├── chat/        ChatMessage (+ repo, controllers WebSocket)
└── shared/
    ├── security/   JwtUtil, JwtAuthenticationFilter, SecurityConfig, AppJwtProperties
    ├── config/     WebSocketConfig
    └── exception/  GlobalExceptionHandler, ResourceNotFoundException, BusinessException
```

## 🗃️ Modèle métier
- **Order** → `Utilisateur` (acheteur) + `List<OrderItem>` ; statut (`OrderStatus`), total `BigDecimal`.
- **OrderItem** → `Product`, avec prix figé (`unitPrice`) au moment de la commande.
- **Product** → `Category` ; champs prix, stock, sku, image, actif.
- **Payment** et **Invoice** → liés à `Order`.
- Montants en `BigDecimal`, statuts en enums, timestamps Hibernate.

## 🔐 Sécurité
- `JwtAuthenticationFilter` lit `Authorization: Bearer <token>`, valide le JWT, peuple le `SecurityContext` (rôle).
- Session **stateless**, CORS autorisé pour `http://localhost:4200`.
- Catalogue en lecture (`GET /api/products`) **public** ; le reste exige un token.
- `register` **interdit l'auto-attribution du rôle admin** (rétrogradé en `client`).
- Refresh tokens hachés (SHA-256) en base, rotation à chaque refresh.

## 🌐 Principaux endpoints
| Méthode | Endpoint | Accès |
|---|---|---|
| POST | `/auth/register` · `/auth/login` · `/auth/refresh` · `/auth/logout` | public |
| GET | `/api/products`, `/api/products/{id}` | public |
| POST/PUT/DELETE | `/api/products/**` | authentifié |
| POST | `/api/orders` (acheteur = JWT) | authentifié |
| GET | `/api/orders`, `/api/orders/{id}` | authentifié |
| POST | `/api/orders/{id}/status`, `/api/orders/{id}/cancel` | authentifié |
| POST/GET | `/api/payments/order/{orderId}` | authentifié |
| WS | `/ws` (STOMP, topics `/topic/*`) | — |

## ⚙️ Configuration (`src/main/resources/application.properties`)
- Base : `jdbc:postgresql://localhost:5432/plateforme_services`
- `spring.jpa.hibernate.ddl-auto=create` → recrée le schéma au démarrage (repasser en `update` pour conserver les données).
- `data.sql` chargé automatiquement (jeu de démo : 2 utilisateurs, 5 catégories, 10 produits, commandes, paiement, facture).
- Le dossier `database/init_demo.sql` contient le script SQL complet (DROP/CREATE + données) en alternative manuelle.
- Secrets JWT/DB en clair (⚠️ à externaliser en variables d'environnement avant prod).

## ▶️ Lancer
```bash
# 1. PostgreSQL doit tourner, base "plateforme_services" créée
# 2. Démarrer l'API (port 8080)
./mvnw spring-boot:run
```

## 👤 Comptes de démo
| Email | Mot de passe | Rôle |
|---|---|---|
| `client@demo.com` | `password` | client |
| `admin@demo.com`  | `password` | admin |

## ⚠️ Améliorations connues
- Externaliser les secrets (JWT, mot de passe DB).
- Passer en **Flyway** + `ddl-auto=validate` pour la prod.
- Ajouter `@PreAuthorize` pour durcir l'autorisation par rôle.
- Exposer un `CategoryController` (`GET /api/categories`).
- Le paiement est **simulé** (PSP à intégrer : Stripe/PayPal).
