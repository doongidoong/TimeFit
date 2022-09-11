import React from "react";
import { Link } from "react-router-dom";
import treadmill from "../assets/image/treadmill.jpg";
import styled from "styled-components";
import Button from "../components/common/Button";

const Main = (props) => {
  return (
    <>
      <Background>
        <TextBackground>
          <Title>운동기구를 예약해서 줄 서는 시간을 줄여요.</Title>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "5rem",
            }}
          >
            <Button>예약하기</Button>
            <Button>헬스장 찾기</Button>
          </div>
        </TextBackground>
      </Background>
    </>
  );
};

const Background = styled.div`
  width: 100%;
  height: 100vh;

  position: relative;
  ::before {
    content: "";
    background-image: url(${treadmill});
    background-repeat: no-repeat;
    background-size: auto;
    background-position: center;
    opacity: 0.5;
    position: absolute;
    top: 0px;
    left: 0px;
    right: 0px;
    bottom: 0px;
  }
`;
const TextBackground = styled.div`
  position: relative;
  top: 0px;
  left: 0px;
  right: 0px;
  bottom: 0px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 30px;
  width: 100%;
  height: 100%;
`;
const Title = styled.div`
  font-family: SLEIGothicTTF;
  font-size: 40px;
`;

export default Main;
