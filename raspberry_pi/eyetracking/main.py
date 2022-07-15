import cv2
import numpy as np
import dlib

global count_left
global count_right

global avg_sclera_left
global avg_sclera_right

#얼굴 위치 출력
def print_face(shape, gray):
    frontal_face = np.array([[shape.part(19).x, shape.part(19).y],
                             [shape.part(0).x, shape.part(0).y],
                             [shape.part(1).x, shape.part(1).y],
                             [shape.part(2).x, shape.part(2).y],
                             [shape.part(3).x, shape.part(3).y],
                             [shape.part(4).x, shape.part(4).y],
                             [shape.part(5).x, shape.part(5).y],
                             [shape.part(6).x, shape.part(6).y],
                             [shape.part(7).x, shape.part(7).y],
                             [shape.part(8).x, shape.part(8).y],
                             [shape.part(9).x, shape.part(9).y],
                             [shape.part(10).x, shape.part(10).y],
                             [shape.part(11).x, shape.part(11).y],
                             [shape.part(12).x, shape.part(12).y],
                             [shape.part(13).x, shape.part(13).y],
                             [shape.part(14).x, shape.part(14).y],
                             [shape.part(15).x, shape.part(15).y],
                             [shape.part(16).x, shape.part(16).y],
                             [shape.part(24).x, shape.part(24).y]], np.int32)
    cv2.polylines(gray, [frontal_face], True, (255, 255, 0))

#눈동자 마스킹
def eye_position(shape, gray, left, right):
    #threshold 값
    thresh_value = 70
    gaze_left = 2
    gaze_right = 2

    #동공 마스킹
    mask = np.zeros(gray.shape[:2], dtype=np.uint8)
    cv2.fillPoly(mask, [left], 255)
    cv2.fillPoly(mask, [right], 255)
    eye = cv2.bitwise_and(gray, gray, mask=mask)

    _, _thresh = cv2.threshold(eye, thresh_value, 255, cv2.THRESH_BINARY)

    #thresh = cv2.erode(thresh, None, iterations=1)
    #thresh = cv2.dilate(thresh, None, iterations=3)
    #thresh = cv2.medianBlur(thresh, 1)

    #print("left : " + str(gaze_left))
    #print("right : " + str(gaze_right))

    return _thresh

#바라보는방향
def gaze_check(thresh, mid, right=False):
    #좌우 민감도
    sensitivity_right = 250
    sensitivity_left = 220

    #흰자 평균값
    global avg_sclera_left
    global avg_sclera_right

    global count_left
    global count_right

    #눈 중앙 기준 흰자 영역 왼쪽 오른쪽 contouring
    cnts_left, _ = cv2.findContours(thresh[:, 0:mid], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cnts_right, _ = cv2.findContours(thresh[:, mid:], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    #contourArea 큰 영역의 반대 방향이 응시 방향
    if len(cnts_left) != 0 and len(cnts_right) != 0:

        #cv2.drawContours(gray, cnts_left, 0, (0, 0, 255), 2)
        #cv2.drawContours(gray, cnts_right, 0, (0, 0, 255), 2)

        cnt_left = max(cnts_left, key=cv2.contourArea)
        cnt_right = max(cnts_right, key=cv2.contourArea)

        area_left = cv2.contourArea(cnt_left)
        area_right = cv2.contourArea(cnt_right)

        if right:
            if area_left > 50 and area_right > 50 and count_right < 10:
                count_right += 1
                avg_sclera_left[1] += int(area_left)
                avg_sclera_right[1] += int(area_right)
            elif count_right == 10:
                count_right += 1
                avg_sclera_right[1] = avg_sclera_right[1] / count_right
                avg_sclera_left[1] = avg_sclera_left[1] / count_right
                print("avg_right_eye_sclera_area : " + str(avg_sclera_right[1] + avg_sclera_left[1]))
        elif right == False:
            if area_left > 50 and area_right > 50 and count_left < 10:
                count_left += 1
                avg_sclera_left[0] += int(area_left)
                avg_sclera_right[0] += int(area_right)
            elif count_left == 10:
                count_left += 1
                avg_sclera_right[0] = avg_sclera_right[0] / count_left
                avg_sclera_left[0] = avg_sclera_left[0] / count_left
                print("avg_left_eye_sclera_area : " + str(avg_sclera_right[0] + avg_sclera_left[0]))

        #print("area_left: " + str(area_left))
        #print("area_right: " + str(area_right))
        #print("\n")

        if int(area_left - area_right) > sensitivity_left:
            #print("watch left")
            return 1
        elif int(area_right - area_left) > sensitivity_right:
            #print("watch right")
            return 0

        """
        cnts, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

        try:
            cnt = max(cnts, key=cv2.contourArea)
            M = cv2.moments(cnt)
            cx = int(M['m10']/M['m00'])
            cy = int(M['m01']/M['m00'])
            if right:
                cx += mid

            cv2.circle(img, (cx, cy), 4, (0, 0, 255), 2)

            if eye_mid > cx and (eye_mid - cx) >= sensitivity_left:
                #left
                return 1
            elif eye_mid < cx and (cx - eye_mid) >= sensitivity_right:
                #right
                return 0
        except:
            pass
        """

#집중도 체크
def concent(thresh, mid):
    #흰자 크기 민감도
    sclera_sensitivity = 1000
    area_sclera = 0

    #이전 10번의 평균값 저장한 뒤 평균과 비교


    #눈 중앙 기준 흰자 영역 왼쪽 오른쪽 contouring
    cnts_left, _ = cv2.findContours(thresh[:, 0:mid], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cnts_right, _ = cv2.findContours(thresh[:, mid:], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    #contourArea 일정 크기 이상 ->
    if len(cnts_left) != 0 and len(cnts_right) != 0:

        #cv2.drawContours(gray, cnts_left, 0, (0, 0, 255), 2)
        #cv2.drawContours(gray, cnts_right, 0, (0, 0, 255), 2)

        cnt_left = max(cnts_left, key=cv2.contourArea)
        cnt_right = max(cnts_right, key=cv2.contourArea)

        area_left = cv2.contourArea(cnt_left)
        area_right = cv2.contourArea(cnt_right)

        #흰자 영역
        area_sclera = area_left + area_right
        #print("area_sclera : " + str(area_sclera))

        #흰자 영역이 매우 클 경우
        #1.시야 범위가 독서대 바깥으로 이동
        #2.눈을 감은경우
        if area_sclera >= sclera_sensitivity:
            #집중
            return True
        else:
            #집중x
            return False

def main():
    global count_left
    global count_right
    global avg_sclera_left
    global avg_sclera_right

    cap = cv2.VideoCapture(0)
    detector = dlib.get_frontal_face_detector()
    predictor = dlib.shape_predictor("shape_68.dat")

    #sclera 평균 연산 카운트용
    count_left = 0
    count_right = 0

    #sclera 평균 넓이 좌, 우
    avg_sclera_left = np.array([0, 0])
    avg_sclera_right = np.array([0, 0])

    while True:
        _, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        detector = dlib.get_frontal_face_detector()
        faces = detector(gray)

        #얼굴 인식
        for face in faces:
            shape = predictor(gray, face)

            left = np.array([[shape.part(36).x, shape.part(36).y],
                             [shape.part(37).x, shape.part(37).y],
                             [shape.part(38).x, shape.part(38).y],
                             [shape.part(39).x, shape.part(39).y],
                             [shape.part(40).x, shape.part(40).y],
                             [shape.part(41).x, shape.part(41).y]], np.int32)

            right = np.array([[shape.part(42).x, shape.part(42).y],
                              [shape.part(43).x, shape.part(43).y],
                              [shape.part(44).x, shape.part(44).y],
                              [shape.part(45).x, shape.part(45).y],
                              [shape.part(46).x, shape.part(46).y],
                              [shape.part(47).x, shape.part(47).y]], np.int32)

            #얼굴 범위 출력
            #print_face(shape, gray)
            _thresh = eye_position(shape, gray, left, right)

            mid = (shape.part(42).x + shape.part(39).x) // 2

            mid_left = (shape.part(36).x + shape.part(39).x) // 2
            mid_right = (shape.part(42).x + shape.part(45).x) // 2

            gaze_left = gaze_check(_thresh[:, 0:mid], mid_left)
            gaze_right = gaze_check(_thresh[:, mid:], (mid_right - mid), True)

            con_left = concent(_thresh[:, 0:mid], mid_left)
            con_right = concent(_thresh[:, mid:], (mid_right - mid))

            # 정확성 높이기 위해 양쪽 눈 응시 방향 일치 시 움직임 인식
            if gaze_left == 1 and gaze_right == 1:  # 0일때
                print("watch left")
            elif gaze_left == 0 and gaze_right == 0:  # 1일때
                print("watch right")
            else:
                pass

            if con_right and con_left:
                print("과도하게 눈을 움직임")

            cv2.imshow("Thresh", _thresh)

        key = cv2.waitKey(1)

        #ESC 입력 시 종료
        if key == 27:
            break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
