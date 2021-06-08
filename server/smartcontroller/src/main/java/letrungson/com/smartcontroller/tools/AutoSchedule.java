package letrungson.com.smartcontroller.tools;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.service.Database;

public class AutoSchedule {
	public static List<Room> listRoom;
	public static List<Schedule> listSchedule;
	public static List<Device> listDevice;
	public static Room room;
	public static String finalIsTurnDeviceOn;

	public static void autoTurnOnOffDevicebySchedule() {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		// tạo list room
		listRoom = new ArrayList<Room>();
		Query roomsQuery = database.getReference("rooms");
		roomsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			public void onDataChange(DataSnapshot roomsSnapshot) {
				listRoom.clear();
				for (DataSnapshot roomData : roomsSnapshot.getChildren()) {
					String roomId = roomData.getKey();
					Room room = roomData.getValue(Room.class);
					room.setRoomId(roomId);
					listRoom.add(room);
				}
				// có list room, duyệt từng room để bật tắt
				for (Room r : listRoom) {
					room = r;
					// tạo list schedule trong phòng đó
					listSchedule = new ArrayList<Schedule>();
					Query schedulesQuery = database.getReference("schedules").orderByChild("roomId")
							.equalTo(room.getRoomId());
					schedulesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
						public void onDataChange(DataSnapshot schedulesSnapshot) {
							listSchedule.clear();
							for (DataSnapshot scheduleData : schedulesSnapshot.getChildren()) {
								Schedule schedule = scheduleData.getValue(Schedule.class);
								if (1 == 1) {// sau này sửa thành schedule.getState = 1
									String scheduleId = scheduleData.getKey();
									schedule.setScheduleId(scheduleId);
									listSchedule.add(schedule);
								}
							}
							// đã có list schedule, cần duyệt các schedule để tìm ra schedule
							// tương ứng thời gian hiện tại, vì chỉ có 1 schedule như thế nên khi tìm ra,
							// tính toán và tạo một biến boolean lưu bật hoặc tắt, sau đó break vòng duyệt
							// schedule

							// thiết lập các biến thời gian hiện tại
							Calendar nowTime = Calendar.getInstance();
							int dayofWeek = nowTime.get(Calendar.DAY_OF_WEEK);
							// sửa lại dayofWeek cho đúng format (dayofWeek cũ thứ 2 = 2, dayofWeek mới thứ
							// 2 = 0)
							if (dayofWeek == 1)
								dayofWeek = 8;
							dayofWeek -= 2;
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
							String nowTimeHHmm = simpleDateFormat.format(nowTime.getTime());
							int hour, minute;
							hour = Integer.parseInt(nowTimeHHmm.substring(0, 2));
							minute = Integer.parseInt(nowTimeHHmm.substring(3, 5));
							String isTurnDeviceOn = "0";
							// duyệt các schedule
							boolean setTargetTemp = false;
							for (Schedule sche : listSchedule) {
								if (sche.getRepeatDay().charAt(dayofWeek) == '1') {
									int startHour, startMinute;
									int endHour, endMinute;
									startHour = Integer.parseInt(sche.getStartTime().substring(0, 2));
									startMinute = Integer.parseInt(sche.getStartTime().substring(3, 5));
									endHour = Integer.parseInt(sche.getEndTime().substring(0, 2));
									endMinute = Integer.parseInt(sche.getEndTime().substring(3, 5));
									boolean isRightTime = false;
									if (startHour < hour && hour < endHour) {
										isRightTime = true;
									} else if (startHour == hour && hour != endHour) {
										if (startMinute <= minute) {
											isRightTime = true;
										}
									} else if (startHour != hour && hour == endHour) {
										if (minute <= endMinute) {
											isRightTime = true;
										}
									} else if (startHour == hour && hour == endHour) {
										if (startMinute <= minute && minute <= endMinute) {
											isRightTime = true;
										}
									}
									if (isRightTime) {
										// set target temp của phòng theo schedule
										if (!room.getRoomTargetTemp().equals(String.valueOf(sche.getTemp()))) {
											room.setRoomTargetTemp(String.valueOf(sche.getTemp()));
											database.getReference().child("rooms").child(room.getRoomId())
													.child("roomTargetTemp")
													.setValueAsync(String.valueOf(sche.getTemp()));
										}
										setTargetTemp = true;
										if (Integer.parseInt(room.getRoomTargetTemp()) < Integer
												.parseInt(room.getRoomCurrentTemp())) {
											isTurnDeviceOn = "1";
										}
										// vì đã đúng schedule nên sau khi lưu biến isTurnDeviceOn, break khỏi vòng lặp
										// schedule
										break;
									}
									// không break, duyệt tiếp những schedule còn lại
								}
							}
							if (setTargetTemp == false) {
								room.setRoomTargetTemp("");
								database.getReference().child("rooms").child(room.getRoomId())
										.child("roomTargetTemp").setValueAsync("");
							}
							// tạo list device trong phòng đó
							listDevice = new ArrayList<Device>();
							Query devicesQuery = database.getReference("devices").orderByChild("roomId")
									.equalTo(room.getRoomId());
							finalIsTurnDeviceOn = isTurnDeviceOn;
							devicesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
								public void onDataChange(DataSnapshot devicesSnapshot) {
									listDevice.clear();
									for (DataSnapshot deviceData : devicesSnapshot.getChildren()) {
										Device device = deviceData.getValue(Device.class);
										if (device.getType().compareTo("Air Conditioner") == 0) {
											String deviceId = deviceData.getKey();
											device.setDeviceId(deviceId);
											listDevice.add(device);
										}
									}
									// đã có list device, duyệt list device, mỗi device thay đổi trên firebase và
									// adafruit theo biến IsTurnDeviceOn đã lưu
									for (Device device : listDevice) {
										if (device.getState().compareTo(finalIsTurnDeviceOn) != 0) {
											Database.updateDevice(device.getDeviceId(), finalIsTurnDeviceOn);
											Database.addLog(device.getDeviceId(), finalIsTurnDeviceOn);
										}
									}
								}

								public void onCancelled(DatabaseError error) {
								}
							});
						}

						public void onCancelled(DatabaseError error) {
						}
					});
					// kết thúc 1 phòng, tiếp tục vòng for bên ngoài để duyệt phòng tiếp theo
				}
			}

			public void onCancelled(DatabaseError error) {

			}
		});
	}
}
