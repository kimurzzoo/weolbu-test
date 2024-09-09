weolbu-test

API 스펙

1. POST /member/register
    - request
        - name: String
        - email: String
        - phoneNumber: String
        - password: String,
        - memberType: "STUDENT" or "TEACHER"
    - response
        - 정상
            - Status 200
            - 없음
        - 이름이 없거나, 회원 유형, 이메일, 비밀번호, 전화번호가 형식에 맞지 않는 경우
            - Status 400
            - 없음
    - description
        - 회원가입 API입니다.
        - 이메일과 전화번호는 형식에 맞아야 하고, 비밀번호는 6자 이상 10자 이하, 영문 대, 소문자, 숫자 중 두 가지 이상의 조합이어야 합니다.
        - 회원 유형은 학생, 강사 둘 중 하나여야 합니다.

#     

2. POST /member/login
    - request
        - email: String
        - password: String
    - response
        - 정상
            - Status 200
            - accessToken: String
        - 이메일, 비밀번호가 형식에 맞지 않거나, 가입하지 않은 회원의 이메일을 입력했거나, 비밀번호를 틀린 경우
            - Status 400
            - 없음
    - description
        - 로그인 API입니다.
        - 이메일은 형식에 맞아야 하고, 비밀번호는 6자 이상 10자 이하, 영문 대, 소문자, 숫자 중 두 가지 이상의 조합이어야 합니다.
        - 이미 가입된 회원이어야 하고, 비밀번호가 맞아야 합니다.

#

3. POST /lecture/teacher/register
    - request
        - name: String
        - maxStudentCnt: Int
        - price: Long
    - response
        - 정상
            - Status 200
            - 없음

#

4. GET /lecture/list

#

5. POST /lecture/enroll