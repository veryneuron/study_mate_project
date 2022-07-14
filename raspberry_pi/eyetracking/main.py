import cv2
import numpy as np
import dlib

#test

#바라보는방향
def gaze_check(shape, thresh, mid, img, right_eye=False):
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

    #좌우 민감도
    sensitivity_right = 250
    sensitivity_left = 200

    if right_eye:
        mid = (shape.part(42).x + shape.part(45).x) // 2
    else:
        mid = (shape.part(36).x + shape.part(39).x) // 2


    #눈 중앙 기준 흰자 영역 왼쪽 오른쪽 contouring
    cnts_left, _ = cv2.findContours(thresh[:, 0: mid], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cnts_right, _ = cv2.findContours(thresh[:, mid:], cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    #contourArea 큰 영역의 반대 방향이 응시 방향
    if len(cnts_left) != 0 and len(cnts_right) != 0:

        cnt_left = max(cnts_left, key=cv2.contourArea)
        cnt_right = max(cnts_right, key=cv2.contourArea)

        area_left = cv2.contourArea(cnt_left)
        area_right = cv2.contourArea(cnt_right)

        #print("area_left: " + str(area_left))
        #print("area_right: " + str(area_right))
        #print("\n")

        if int(area_left - area_right) > sensitivity_left:
            print("watch left")
            return [thresh, 1]
        elif int(area_right - area_left) > sensitivity_right:
            print("watch right")
            return [thresh, 0]

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
def eye_position(shape, gray):
    #threshold 값
    thresh_value = 70
    gaze_left = 0
    gaze_right = 0

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

    #동공 마스킹
    mask = np.zeros(gray.shape[:2], dtype=np.uint8)
    cv2.fillPoly(mask, [left], 255)
    cv2.fillPoly(mask, [right], 255)
    eye = cv2.bitwise_and(gray, gray, mask=mask)

    _, thresh = cv2.threshold(eye, thresh_value, 255, cv2.THRESH_BINARY)


    #thresh = cv2.erode(thresh, None, iterations=1)
    #thresh = cv2.dilate(thresh, None, iterations=3)
    #thresh = cv2.medianBlur(thresh, 1)

    mid = (shape.part(42).x + shape.part(39).x) // 2

    return [thresh, mid]

    #mid_left = (shape.part(36).x + shape.part(39).x) // 2
    #mid_right = (shape.part(42).x + shape.part(45).x) // 2


def main():

    cap = cv2.VideoCapture(0)
    detector = dlib.get_frontal_face_detector()
    predictor = dlib.shape_predictor("shape_68.dat")

    while True:
        _, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        detector = dlib.get_frontal_face_detector()
        faces = detector(gray)

        #얼굴 인식
        for face in faces:
            shape = predictor(gray, face)
            #얼굴 범위 출력
            #print_face(shape, gray)
            [_thresh, mid] = eye_position(shape, gray)

            # 응시 방향
            [_thresh, gaze_left] = gaze_check(shape, _thresh[:, 0:mid], mid, gray)
            [_thresh, gaze_right]  = gaze_check(shape, _thresh[:, mid:], mid, gray, True)

            # 정확성 높이기 위해 양쪽 눈 응시 방향 일치 시 움직임 인식
            if gaze_left and gaze_right:  # 0일때
                print("watch left")
            elif gaze_left == 0 and gaze_right == 0:  # 1일때
                print("watch right")
            else:
                pass

            cv2.imshow("Thresh", _thresh)


        key = cv2.waitKey(1)

        #ESC 입력 시 종료
        if key == 27:
            break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
