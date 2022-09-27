import update from "immutability-helper";
import { useCallback, useState, useEffect } from "react";
import { useDrop } from "react-dnd";
import { DraggableItem } from "./DraggableItem.js";
import { snapToGrid as doSnapToGrid } from "./snapToGrid.js";

const handleStatus = (startTop, top) => {
  let status = "";
  if (+startTop < +top) {
    status = "UpToDown";
  } else {
    status = "DownToDown";
  }
  return status;
};

export default ({
  fromItems,
  items,
  setItems,
  setListItems,
  type,
  heightGap,
  layoutTop,
}) => {
  const moveBox = useCallback(
    (id, left, top, status) => {
      status === "UpToDown"
        ? setItems([
            ...items,
            {
              component: fromItems[id].component,
              top: top,
              left: left,
            },
          ])
        : setItems(
            update(items, {
              [id]: {
                $merge: { left, top },
              },
            })
          );
    },
    [items, layoutTop]
  );

  const [, drop] = useDrop(
    () => ({
      accept: type,
      drop(item, monitor) {
        const predicted = monitor.getSourceClientOffset();
        console.log(monitor.getInitialClientOffset()); //드래그 시작될 시점의 포인터 위치
        console.log(monitor.getInitialSourceClientOffset()); //드래그 시작될 시점의 컴포넌트 위치
        // console.log(monitor.getClientOffset()); //마지막으로 드래그될 시점의 위치
        // console.log(monitor.getDifferenceFromInitialOffset()); //포인터의 마지막 위치와 드래그 시작된 위치의 차
        // console.log(monitor.getSourceClientOffset()); //예상되는 위치
        console.log("-------------------------");
        const start = monitor.getInitialSourceClientOffset();
        const status = handleStatus(start.y, layoutTop);
        console.log(status);
        const delta = monitor.getDifferenceFromInitialOffset();
        let left = Math.round(item.left + delta.x);
        let top =
          status === "UpToDown"
            ? Math.round(item.top + delta.y - heightGap)
            : Math.round(item.top + delta.y);

        moveBox(item.id, left, top, status);
        return undefined;
      },
      collect: (monitor) => ({
        canDrop: monitor.canDrop(),
      }),
    }),
    [moveBox]
  );
  return (
    <div
      ref={drop}
      style={{ width: "100%", height: "100%", position: "relative" }}
    >
      {Object.keys(items).map((key) => {
        return <DraggableItem key={key} id={key} type={type} {...items[key]} />;
      })}
    </div>
  );
};