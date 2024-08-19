 <h1>Spring WebFlux Security with JWT 🛡️ </h1>

  <p>This project is an implementation of JWT (JSON Web Tokens) based authentication and authorization using Spring WebFlux and Spring Security. All request handling is fully reactive, utilizing handlers instead of traditional REST controllers.</p>

  <h2>Requirements</h2>
    <ul>
        <li><strong>Java 17</strong> or higher</li>
        <li><strong>Maven 3.6.3</strong> or higher</li>
        <li><strong>MongoDB</strong> (configured as a reactive database)</li>
    </ul>

  <h2>Main Dependencies</h2>
    <p>This project uses the following key dependencies:</p>
    <ul>
        <li><code>spring-boot-starter-webflux</code>: For developing reactive applications with Spring WebFlux.</li>
        <li><code>spring-boot-starter-security</code>: For handling security in the application.</li>
        <li><code>spring-boot-starter-data-mongodb-reactive</code>: For reactive integration with MongoDB.</li>
        <li><code>jjwt-api</code>, <code>jjwt-impl</code>, <code>jjwt-jackson</code>: For creating and validating JWTs.</li>
    </ul>

  <h2>Project Structure</h2>
    <p>The project is organized into the following packages:</p>
    <ul>
        <li><strong><code>config.security</code></strong>: Contains the security configuration and JWT-related components, such as <code>AuthConverter</code>, <code>AuthManger</code>, and <code>BearerToken</code>.</li>
        <li><strong><code>dto</code></strong>: Contains data transfer objects (DTOs) used in requests and responses, such as <code>SaveUser</code>, <code>SaveCategory</code>, and <code>SaveProduct</code>.</li>
        <li><strong><code>exception</code></strong>: Includes custom exceptions like <code>InvalidPasswordException</code> and <code>ObjectNotFoundException</code>, as well as global error handling with <code>GlobalErrorGeneral</code>.</li>
        <li><strong><code>handler</code></strong>: Contains the reactive handlers that process HTTP requests. Examples include <code>AuthenticationHandler</code>, <code>CategoriaHandler</code>, <code>CustomerHandler</code>, and <code>ProductHandler</code>.</li>
        <li><strong><code>persistence</code></strong>:
            <ul>
                <li><strong><code>documents</code></strong>: Defines the entity classes stored in MongoDB, such as <code>Category</code>, <code>Product</code>, <code>RegisteredUser</code>, <code>ReqLogin</code>, and <code>Users</code>.</li>
                <li><strong><code>repository</code></strong>: Contains interfaces for MongoDB reactive repositories, such as <code>CategoryDao</code>, <code>ProductDao</code>, and <code>UserRepository</code>.</li>
            </ul>
        </li>
        <li><strong><code>service</code></strong>:
            <ul>
                <li><strong><code>auth</code></strong>: Services related to authentication, such as <code>AuthenticationService</code>, <code>JwtAuthenticationToken</code>, and <code>JwtService</code>.</li>
                <li><strong><code>imp</code></strong>: Implementations of application services, such as <code>CategoryServiceImp</code>, <code>ProductServiceImp</code>, and <code>UserServiceImpl</code>.</li>
            </ul>
        </li>
        <li><strong><code>util</code></strong>: Utility classes, such as <code>Role</code> and <code>RolePermission</code>.</li>
        <li><strong><code>RouterFunctionConfig</code></strong>: Configuration of functional routes for handling HTTP requests.</li>
    </ul>

  <h2>Configuration</h2>
    <p>Make sure MongoDB is running and properly configured. Database connection details and other application configurations can be modified in the <code>application.properties</code> or <code>application.yml</code> file.</p>

  <h2>Features</h2>
    <ul>
        <li><strong>JWT Authentication</strong>: Users can authenticate with credentials and receive a JWT token.</li>
        <li><strong>Role-Based Authorization</strong>: Access to specific routes based on user roles.</li>
        <li><strong>WebFlux Security</strong>: Reactive security configuration using JWT filters to protect routes.</li>
        <li><strong>Reactive Handlers</strong>: Non-blocking processing of HTTP requests through <code>ServerRequest</code> and <code>ServerResponse</code>.</li>
    </ul>

   <h2>Running the Application</h2>
    <p>To run the application, ensure all dependencies are installed and execute the following command from the project root:</p>
    <pre><code>mvn spring-boot:run</code></pre>
    <p>This will start the server on the configured port (default: 8080).</p>

  <h2>Usage</h2>
    <ol>
        <li><strong>User Registration</strong>: A <code>POST</code> request can be sent to <code>/api/v1/customers</code> with the user credentials.</li>
        <li><strong>Login</strong>: A <code>POST</code> request can be sent to <code>/auth/authenticate</code> with credentials to receive a JWT token.</li>
        <li><strong>Accessing Protected Routes</strong>: Include the JWT token in the <code>Authorization</code> header with the <code>Bearer</code> prefix to access protected routes.</li>
    </ol>
    <h2>Contributing</h2>
    <p>Contributions are welcome. If you find a bug or have a suggestion, please create an issue or submit a pull request.</p>
