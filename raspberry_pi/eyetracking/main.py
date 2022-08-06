import cv2
import numpy as np
import dlib
from scipy.spatial import distance
import time
from multiprocessing import Process

global count_left
global count_right

# avg_sclera_left[0] : 왼쪽눈 왼쪽 흰자영역
# avg_sclera_left[1] : 오른쪽눈 왼쪽 흰자영역
# avg_sclera_right[0] : 왼쪽눈 오른쪽 흰자영역
# avg_sclera_right[1] : 오른쪽눈 오른쪽 흰자영역
global avg_sclera_left
global avg_sclera_right


#눈동자 마스킹
def eye_position(shape, gray, left, right):
    #threshold 값
    thresh_value = 100

    #동공 마스킹
    mask = np.zeros(gray.shape[:2], dtype=np.uint8)
    cv2.fillPoly(mask, [left], 255)
    cv2.fillPoly(mask, [right], 255)
    eye = cv2.bitwise_and(gray, gray, mask=mask)

    _, _thresh = cv2.threshold(eye, thresh_value, 255, cv2.THRESH_BINARY)

    return _thresh


#흰자영역 평균크기 계산
def calcul_sclera_avg(thresh, mid, right=False):
    global avg_sclera_left
    global avg_sclera_right

    global count_left
    global count_right

    #눈 중앙 기준 흰자 영역 왼쪽 오른쪽 contouring
    cnts_left, _ = cv2.findContours(thresh[:, 0:mid], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cnts_right, _ = cv2.findContours(thresh[:, mid:], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    #contourArea 큰 영역의 반대 방향이 응시 방향
    if len(cnts_left) != 0 and len(cnts_right) != 0:

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
                avg_sclera_right[1] = avg_sclera_right[1] / count_right
                avg_sclera_left[1] = avg_sclera_left[1] / count_right
                count_right += 1
                print("avg_right_eye_sclera_area[left]: " + str(avg_sclera_left[1]))
                print("avg_right_eye_sclera_area[right]: " + str(avg_sclera_right[1]))
                print("right done")
        elif right == False:
            if area_left > 45 and area_right > 45 and count_left < 10:
                count_left += 1
                avg_sclera_left[0] += int(area_left)
                avg_sclera_right[0] += int(area_right)
            elif count_left == 10:
                avg_sclera_right[0] = avg_sclera_right[0] / count_left
                avg_sclera_left[0] = avg_sclera_left[0] / count_left
                count_left += 1
                print("avg_left_eye_sclera_area[left]: " + str(avg_sclera_left[0]))
                print("avg_left_eye_sclera_area[right]: " + str(avg_sclera_right[0]))
                print("left done")


#바라보는방향
def gaze_check(gray, thresh, mid, ratio, right=False):
    # 눈동자 움직임 민감도
    # 0: 좌측 1 : 우측
    # left, right : 흰자영역
    sensitivity_left = np.array([35, 35])
    sensitivity_right = np.array([35, 35])
    #눈감음 민감도
    sensitivity_closed = 12

    ratio = 1 + ratio

    area_left = 0
    area_right = 0

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

        #print("area_left : " + str(area_left))
        #print("area_right : " + str(area_right))

        # avg_sclera_left[0] : 왼쪽눈 왼쪽 흰자영역
        # avg_sclera_left[1] : 오른쪽눈 왼쪽 흰자영역
        # avg_sclera_right[0] : 왼쪽눈 오른쪽 흰자영역
        # avg_sclera_right[1] : 오른쪽눈 오른쪽 흰자영역
        eye_L_sclera_L_change = int(area_left - int(avg_sclera_left[0] * ratio))
        eye_L_sclera_L_base = ((avg_sclera_left[0] - sensitivity_left[0]) * ratio)

        eye_L_sclera_R_change = int(area_right - int(avg_sclera_right[0] * ratio))
        eye_L_sclera_R_base = ((avg_sclera_right[0] - sensitivity_right[0]) * ratio)

        eye_R_sclera_L_change = int(area_left - int(avg_sclera_left[1] * ratio))
        eye_R_sclera_L_base = ((avg_sclera_left[1] - sensitivity_left[1]) * ratio)

        eye_R_sclera_R_change = int(area_right - int(avg_sclera_right[1] * ratio))
        eye_R_sclera_R_base = ((avg_sclera_right[1] - sensitivity_right[1]) * ratio)

        #왼쪽 오른쪽 움직임 avg에 따라 sensitivity 조절하는 방법으로 수정하기
        if right:
            # 왼쪽 쳐다봄 or 오른쪽 쳐다봄
            if eye_R_sclera_L_change > eye_R_sclera_L_base or eye_R_sclera_R_change > eye_R_sclera_R_base:
                return 1
        elif right==False:
            #왼쪽 쳐다봄 or 오른쪽 쳐다봄
            if eye_L_sclera_L_change > eye_L_sclera_L_base or eye_L_sclera_R_change > eye_L_sclera_R_base:
                return 1


#얼굴 크기 변화율 계산
def calcul_ratio(face, base_length):
    face_left = face[0][0]
    face_right = face[1][0]
    face_length = face_right - face_left

    ratio = (face_length - base_length) / base_length
    ratio = round(ratio, 2)

    return ratio

#눈 감음 인식 (EAR 알고리즘)
def eye_aspect_ratio(eye):
    A = distance.euclidean(eye[1], eye[5])
    B = distance.euclidean(eye[2], eye[4])
    C = distance.euclidean(eye[0], eye[3])

    ear = (A+B) / (2.0 * C)

    return ear

def main():
    global count_left
    global count_right

    global avg_sclera_left
    global avg_sclera_right

    eye_ar_thresh = 0.23

    face_count = 0

    start_time = 0
    end_time = 0

    count_con = 0
    count_uncon = 0

    arr_temp = []
    count = 0

    detector = dlib.get_frontal_face_detector()
    predictor = dlib.shape_predictor("shape_68.dat")

    # sclera 평균 연산 카운트용
    count_left = 0
    count_right = 0

    # sclera 평균 넓이 좌, 우
    avg_sclera_left = np.array([0, 0])
    avg_sclera_right = np.array([0, 0])

    # 얼굴 크기 변화율
    rate_of_change = 0
    # 기준 얼굴 크기
    base_length = 0

    cap = cv2.VideoCapture('rtsp://172.30.1.39:8556/unicast')

    while True:
        _, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces = detector(gray)
        cv2.imshow("rtsp", gray)

        # 얼굴 인식
        for face in faces:
            face_count += 1
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

            head = np.array([[shape.part(0).x, shape.part(0).y],
                            [shape.part(16).x, shape.part(16).y],
                            [shape.part(30).x, shape.part(30).y]], np.int32)

            # 얼굴 범위 출력
            _thresh = eye_position(shape, gray, left, right)

            mid = (shape.part(42).x + shape.part(39).x) // 2

            mid_left = (shape.part(36).x + shape.part(39).x) // 2
            mid_right = (shape.part(42).x + shape.part(45).x) // 2

            if count_left < 11 or count_right < 11:
                calcul_sclera_avg(_thresh[:, 0:mid], mid_left)
                calcul_sclera_avg(_thresh[:, mid:], (mid_right - mid), True)

                base_length = shape.part(16).x - shape.part(0).x

            # 평균 흰자영역 계산 후 연산
            if count_left > 10 and count_right > 10:
                rate_of_change = calcul_ratio(head, base_length)

                gaze_left = gaze_check(gray, _thresh[:, 0:mid], mid_left, rate_of_change)
                gaze_right = gaze_check(gray, _thresh[:, mid:], (mid_right - mid), rate_of_change, True)

                ear_left = eye_aspect_ratio(left)
                ear_right = eye_aspect_ratio(right)

                if ear_left <= eye_ar_thresh and ear_right <= eye_ar_thresh:
                    print("eye closed")
                    count_uncon += 1
                    temp = 1
                    count += 1
                else:
                    # 정확성 높이기 위해 양쪽 눈 응시 방향 일치 시 움직임 인식
                    if gaze_left == 1 and gaze_right == 1:
                        print("unfocused")
                        count_uncon += 1
                        temp = 1
                    else:
                        count_con += 1
                        temp = 0
                    count += 1

                if count <= 10:
                    arr_temp.append(temp)
                else:
                    del arr_temp[0]
                    arr_temp.append(temp)

                if len(arr_temp) == 10:
                    if start_time == 0 and count_uncon >= 10 and arr_temp.count(1) > 7:
                        start_time = int(time.time())
                        print("start time : " + str(start_time))
                        count_uncon = 0
                        count_con = 0
                        # 아두이노로 시작 신호 전송
                        # 서버로 시작 신호 전송
                    elif start_time != 0 and count_con >= 10 and arr_temp.count(0) > 7:
                        end_time = int(time.time())
                        count_uncon = 0
                        count_con = 0
                        print("time : " + str(end_time - start_time))
                        start_time = 0
                        end_time = 0
                        # 아두이노로 종료 신호 전송
                        # 서버로 종료 신호 전송

                temp = 0
                cv2.imshow("thresh", _thresh)
                print("")

        if face_count == 0:
            print("absence or turn")
            count_uncon += 1


        if start_time == 0 and count_left > 10 and count_right > 10 and face_count == 0 and count_uncon >= 30:
            start_time = int(time.time())
            count_uncon = 0
            count_con = 0
            print("start time : " + str(start_time))


        face_count = 0

        key = cv2.waitKey(1)
        #ESC 입력 시 종료
        if key == 27:
            break

        #아두이노에서 받은 온습도 기록 서버로 전송

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()