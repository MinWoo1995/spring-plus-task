필수과제 레벨 2 문제 9번까지 구현 하였습니다.


Spring Security 기반 인증 시스템 전환 프로젝트

이 프로젝트는 기존의 수동 인증 로직(Filter, ArgumentResolver)을 Spring Security 표준 아키텍처로 전환하여 시스템의 보안성과 유지보수성을 높이는 데 집중한 과제입니다.

핵심 변경 사항 (Refactoring)

1. Spring Security 프레임워크 도입

보안 설정 중앙화: SecurityConfig를 통해 CSRF 비활성화, Stateless 세션 관리, 경로별 권한(PermitAll, Authenticated)을 통합 관리합니다.

표준 인증 객체 활용: UserDetails, UserDetailsService 인터페이스를 구현하여 스프링 시큐리티의 인증 프로세스를 표준화했습니다.

어노테이션 전환: 커스텀 @Auth 어노테이션을 삭제하고, 표준인 **@AuthenticationPrincipal**을 도입하여 컨트롤러의 가독성과 타입 안정성을 확보했습니다.

2. 인증 필터 최적화

OncePerRequestFilter: 매 요청마다 한 번만 실행되는 JwtSecurityFilter를 구현하여 중복 검증을 방지했습니다.

SecurityContext 관리: 유효한 토큰일 경우 SecurityContextHolder에 인증 정보를 저장하여 시스템 전역에서 유저 정보를 활용할 수 있게 설계했습니다.



트러블슈팅 및 해결 (Troubleshooting)

프로젝트 진행 중 발생한 핵심 문제들과 해결 과정입니다.

Issue 1: 회원가입/로그인 진입 불가 (Not Found Token)

현상: 토큰 없이 접근해야 하는 /auth/** 경로에서 ServerException이 발생하며 접속이 차단됨.

원인: JwtUtil.substringToken() 메서드가 토큰이 없을 때 강제로 예외를 던져, 시큐리티의 permitAll() 설정까지 도달하지 못함.

해결: 토큰 부재 시 null을 반환하도록 로직을 수정하고, 필터 내에서 null 체크를 통해 비인증 요청은 다음 필터 체인으로 자연스럽게 넘어가도록 개선했습니다.

Issue 2: PasswordEncoder Bean 충돌

현상: 애플리케이션 시작 시 passwordEncoder 빈 이름 중복으로 서버 실행 실패.

원인: 프로젝트 내 기존 PasswordEncoder 클래스와 SecurityConfig의 @Bean 메서드 명이 충돌함.

해결: 중복되는 기존 클래스를 삭제하고, 시큐리티 표준인 BCryptPasswordEncoder를 빈으로 등록하여 의존성 주입을 단일화했습니다.

Issue 3: 서블릿 필터 IO 인터페이스 충돌

현상: doFilterInternal 메서드에서 컴파일 에러 발생.

원인: java.io.IOException 대신 JWT 라이브러리의 io.jsonwebtoken.io.IOException을 잘못 임포트함.

해결: 표준 Java IO 패키지로 임포트를 수정하여 서블릿 컨테이너와의 호환성을 맞췄습니다.


최종 결과물 (Key Results)

JWT 인증 성공: 로그인 후 발급받은 토큰으로 Authorization 헤더를 통해 인증이 필요한 API 호출 성공.

기능 정상 작동: 할 일 등록 등 모든 비즈니스 로직에서 @AuthenticationPrincipal을 통한 유저 식별 정상 작동.

클린 코드: 더 이상 사용하지 않는 FilterConfig, AuthUserArgumentResolver, WebConfig 내 관련 설정 등을 모두 주석처리하여 프로젝트 구조를 단순화했습니다.
