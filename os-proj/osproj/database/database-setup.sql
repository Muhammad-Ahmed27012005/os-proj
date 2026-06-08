-- Create database
CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- Create Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    room_type VARCHAR(50) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    capacity INT NOT NULL,
    description TEXT,
    amenities TEXT,
    image_url VARCHAR(255),
    INDEX idx_room_type (room_type),
    INDEX idx_available (available)
);

-- Create Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guest_name VARCHAR(100) NOT NULL,
    guest_email VARCHAR(100) NOT NULL,
    guest_phone VARCHAR(20) NOT NULL,
    room_id BIGINT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    booking_date DATE NOT NULL,
    special_requests TEXT,
    number_of_guests INT,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_dates (check_in_date, check_out_date)
);

-- Create Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    burst_time INT NOT NULL,
    priority INT NOT NULL,
    arrival_time INT NOT NULL,
    waiting_time INT DEFAULT 0,
    turnaround_time INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    task_type VARCHAR(50),
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_type (task_type)
);

-- Insert sample rooms
INSERT INTO rooms (room_number, room_type, price_per_night, available, capacity, description, amenities) VALUES
('101', 'DELUXE', 299.00, TRUE, 2, 'Luxurious deluxe room with city view', 'WiFi, TV, Mini Bar, AC'),
('102', 'DELUXE', 299.00, TRUE, 2, 'Elegant deluxe room with garden view', 'WiFi, TV, Mini Bar, AC'),
('201', 'SUITE', 599.00, TRUE, 4, 'Spacious executive suite with separate living area', 'WiFi, TV, Mini Bar, AC, Jacuzzi'),
('301', 'PRESIDENTIAL', 1299.00, TRUE, 6, 'Ultimate presidential suite with panoramic views', 'WiFi, TV, Mini Bar, AC, Jacuzzi, Butler Service'),
('401', 'PENTHOUSE', 2499.00, TRUE, 8, 'Luxury penthouse with private terrace', 'WiFi, TV, Mini Bar, AC, Private Pool, Butler Service');

-- Insert sample tasks
INSERT INTO tasks (task_name, burst_time, priority, arrival_time, task_type, status) VALUES
('Room Cleaning 101', 3, 1, 0, 'HOUSEKEEPING', 'PENDING'),
('AC Repair 201', 5, 2, 1, 'MAINTENANCE', 'PENDING'),
('Room Service 301', 2, 1, 2, 'ROOM_SERVICE', 'PENDING'),
('Lobby Cleaning', 4, 3, 0, 'HOUSEKEEPING', 'PENDING'),
('Guest Check-in', 1, 1, 3, 'CONCIERGE', 'PENDING');