#!/usr/bin/python3

# Imports
from kafka import KafkaProducer  # pip install kafka-python
import random
from datetime import datetime
from time import sleep

# Predefined data for the expenses table
EMPLOYEES = [23,7782,7839,7934,7566,7788,7876,7902,49999,56000,7499,7521,7654,7698,7844,7900 ]
DESCRIPTIONS = [
    {"description": "macaroni", "etype": "food"},
    {"description": "jacket", "etype": "clothe"},
    {"description": "car", "etype": "vehicle"},
]
TOPIC_NAME = "SAU"  # Kafka topic name

# Set up the Kafka producer
producer = KafkaProducer(bootstrap_servers='localhost:9092')

count = 1

# Function to generate a random expense
def generate_expense():
    empno = random.choice(EMPLOYEES)  # Randomly select employee number
    date_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # Current timestamp
    expense = random.choice(DESCRIPTIONS)  # Randomly select a description and type
    payment = round(random.uniform(10, 500), 2)  # Random payment amount between 10 and 500
    
    # Combine into a dictionary
    expense_data = {
        "empno": empno,
        "date_time": date_time,
        "description": expense["description"],
        "etype": expense["etype"],
        "payment": payment,
    }
    return expense_data

# Infinite loop to produce messages
try:
    while True:
        # Generate expense data
        expense = generate_expense()
        
        # Create a CSV-like structure for the Kafka message
        msg = f'{expense["empno"]},{expense["date_time"]},{expense["description"]},{expense["payment"]},{expense["etype"]}'
        
        # Send the message to Kafka
        producer.send(TOPIC_NAME, bytes(msg, encoding='utf8'))
        print(f'Sent to Kafka (#{count}): {msg}')
        
        count += 1
        sleep(3)  # Sleep for 1 second
except KeyboardInterrupt:
    print("\nProducer stopped by user.")
finally:
    producer.close()
    