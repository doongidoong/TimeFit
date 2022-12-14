//기구 선택하면 뜨는 예약 팝업

import Popper from "@mui/material/Popper";
import { useState, useContext, useEffect } from "react";
import { ReservePopperContext } from "contexts/reservePopperContext";
import styled from "styled-components";
import { MACHINE_NAME } from "constants/center";
import TimeColumn from "pages/reserve/TimeColumn";
import axios from "axios";
import { useAuth } from "hooks/useAuthContext";

export default (props) => {
  const reservePopper = useContext(ReservePopperContext);
  const { id, anchorEl, name } = reservePopper;
  const [reservation, setReservation] = useState([]);
  const { user } = useAuth();

  //todo : loading spinner 구현
  const handleReservation = async () => {
    try {
      const { data } = await axios.get(`/center/${user.center.id}/reserve`, {
        params: { searchIds: id },
      });
      setReservation(data[0].times);
    } catch (e) {
      console.log(e);
      alert("예약 조회 과정에서 에러가 발생했습니다.");
    }
  };

  //서버에서 데이터 가져와야 함
  useEffect(() => {
    if (!id) return;
    handleReservation();
  }, [id]);
  return (
    <Popper id={id} open={!!id} anchorEl={anchorEl}>
      <WrapperDiv>
        <Title>{MACHINE_NAME[name]}</Title>
        <TimeColumn reservation={reservation} readOnly={true}></TimeColumn>
      </WrapperDiv>
    </Popper>
  );
};

const WrapperDiv = styled.div`
  padding: 10px;
  width: 250px;
  max-height: 350px;
  overflow: auto;
  background-color: white;
  display: flex;
  flex-direction: column;
  gap: 10px;
  border: 1px solid lightgray;
`;

const Title = styled.div`
  font-family: SLEIGothicTTF;
  font-size: 20px;
  text-align: center;
`;
