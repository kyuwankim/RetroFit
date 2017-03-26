# RetroFit
Retrofit은 HTTP API를 자바 인터페이스 형태로 사용할 수 있습니다.

```java
public interface GitHubService {
  @GET("/users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```

Retrofit 클래스로 GitHubService 인터페이스를 구현하여 생성합니다.

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .build();

GitHubService service = retrofit.create(GitHubService.class);

```

각각의 Call 은 GitHubService 를 통하여 동기 또는 비동기하는 HTTP 요청을 원격 웹서버로 보낼 수 있습니다.

```java
Call<List<Repo>> repos = service.listRepos("octocat");
```

HTTP 요청은 어노테이션을 사용하여 명시합니다.

- URL 파라미터 치환과 쿼리 파라미터가 지원됩니다.
- 객체를 요청 body로 변환합니다. (예: JSON, protocol buffers)
- 멀티파트 요청 body와 파일 업로드가 가능합니다.

***API정의***
###요청 메소드

모든 메소드들은 반드시 상대 URL과 요청 메소드를 명시하는 어노테이션을 가지고 있어야합니다. 기본으로 제공하는 요청 메소드 어노테이션은 다음과 같이 5개가 있습니다 : GET, POST, PUT, DELETE, HEAD.

> @GET("/users/list")

정적 쿼리 인자를 URL에 명시할 수도 있습니다.

> @GET("/users/list?sort=desc")

URL 다루기

요청 URL은 동적으로 부분 치환 가능하며, 이는 메소드 매개변수로 변경이 가능합니다. 부분 치환은 영문/숫자로 이루어진 문자열을 { 와 } 로 감싸 정의해줍니다. 반드시 이에 대응하는 @Path 를 메소드 매개변수에 명시해줘야합니다.

```java
@GET("/group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId);
```

쿼리 매개변수도 명시 가능합니다.

```java
@GET("/group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
```

보다 동적이며 다양한 쿼리 매개변수들은 Map 으로도 사용하실 수 있습니다.

```java
@GET("/group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);
```

###요청 본문
HTTP 요청 본문에 객체를 @Body 어노테이션을 통해 명시가 가능합니다.

```java
@POST("/users/new")
Call<User> createUser(@Body User user);
```

이러한 객체들은 Retrofit 인스턴스에 추가된 컨버터에 따라 변환됩니다. 만약 해당 타입에 맞는 컨버터가 추가되어있지 않다면, RequestBody 만 사용하실 수 있습니다.

FORM-ENCODED과 MULTIPART

메소드는 form-encoded 데이터와 multipart 데이터 방식으로 정의 가능합니다.

@FormUrlEncoded 어노테이션을 메소드에 명시하면 form-encoded 데이터로 전송 됩니다. 각 key-value pair의 key는 어노테이션 값에, value는 객체를 지시하는 @Field 어노테이션으로 매개변수에 명시하시면 됩니다.

```java
@FormUrlEncoded
@POST("/user/edit")
Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);
```

Multipart 요청은 @Multipart 어노테이션을 메소드에 명시하시면 됩니다. 각 파트들은 @Part 어노테이션으로 명시합니다.

```java
@Multipart
@PUT("/user/photo")
Call<User> updateUser(@Part("photo") RequestBody photo, @Part("description") RequestBody description);
```

Multipart의 part는 Retrofit 의 컨버터나, RequestBody 를 통하여 직렬화(serialization) 가능한 객체를 사용하실 수 있습니다.


###헤더 다루기
정적 헤더들은 @Headers 어노테이션을 통해 명시할 수 있습니다.

```java
@Headers("Cache-Control: max-age=640000")
@GET("/widget/list")
Call<List<Widget>> widgetList();
```

```java
@Headers({
    "Accept: application/vnd.github.v3.full+json",
    "User-Agent: Retrofit-Sample-App"
})
@GET("/users/{username}")
Call<User> getUser(@Path("username") String username);
```

참고하실 점은, 헤더들은 이름에 기준하여 각각의 값을 덮어씌우지 않습니다. 동일한 이름의 헤더를 추가한다면 모든 헤더들은 동일한 이름으로 모두 요청에 추가됩니다.

동적인 헤더는 @Header 어노테이션을 통해 명시 가능합니다. 반드시 이에 대응하는 @Header 어노테이션을 매개변수에 명시해야합니다. 만약 값이 null이라면 해당 헤더는 추가되지않습니다. 아니라면, 매개변수 객체의 toString 메소드를 호출하여 반환된 값을 헤더의 값으로 추가합니다.

```java
@GET("/user")
Call<User> getUser(@Header("Authorization") String authorization)
```

헤더를 모든 요청마다 추가하셔야 한다면 OkHttp interceptor 를 사용하십시요.

###동기 VS. 비동기

Call 인스턴스는 동기 혹은 비동기로 요청 실행이 가능합니다. 각 인스턴스들은 동기 혹은 비동기중 한가지 방식만 사용가능합니다, 하지만 clone() 메소드를 통해 새 인스턴스를 생성하시면 이전과 다른 방식을 사용 가능합니다.

안드로이드에서의 콜백들은 메인 스레드에서 실행됩니다. JVM에서는, HTTP 요청을 호출한 스레드와 동일한 스레드에서 콜백들이 실행됩니다.


###Retrofit 설정

Retrofit 은 API 인터페이스를 호출 가능한 객체로 바꿔줍니다. 기본적으로 Retrofit은 실행중인 플랫폼에 최적화된 설정을 제공하지만, 사용자정의도 가능합니다.

컨버터

기본적으로, Retrofit은 HTTP 요청 본문을 OkHttp의 ResponseBody 형식과 @Body 에 이용하는 RequestBody 타입만 역직렬화(deserialization) 할 수 있습니다.

컨버터들은 이외의 형식들을 변환해주는 역할을 합니다. 아래에 많이 사용하고 편리한 자사의 컨버터 6개의 라이브러리들을 사용하실 수 있습니다.

- Gson
- Jackson
- Moshi
- Protobuf
- Wire
- Simple XML

그 예로, Gson을 사용하는 GitHubService 인터페이스가 Gson을 역직렬화가 가능하게 GsonConverterFactory 클래스를 통해 컨버터를 추가하는 예제입니다.

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

GitHubService service = retrofit.create(GitHubService.class);
```

###사용자정의 컨버터

만약 API와 통신하는데 Retrofit이 지원하지 않는 형식(e.g. YAML, txt, custom format) 이거나 현재 사용가능한 형식이지만 다른 라이브러리를 통해 구현하시길 원하신다면, 쉽게 나만의 컨버터를 만들 수 있습니다. Converter.Factory 클래스 를 상속하여 만드시고, 이를 Retrofit 객체를 만드실때 추가하시면됩니다.