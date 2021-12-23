# sam-app-extract

This is an AWS Lambda function and API Gateway that converts mcgill exam schedule into JSON.

Calling the API:

```bash
curl --location --request POST 'https://[your_endpoint]/extract' \
--header 'Content-Type: application/json' \
--data-raw '{"url":"https://www.mcgill.ca/exams/files/exams/december_2021_final_exam_schedule_-_with_rooms_v7.pdf"}'
```

Sample Return:

```json
{
  "exams": [
    {
      "term": "fall21",
      "course": "ACCT 351",
      "section": "001",
      "title": "Intermediate Financial Acct 1",
      "type": "IN-PERSON - IN-DEPARTMENT EXAM - D.T. CAMPUS",
      "start": "15-Dec-2021 at 2:00 PM",
      "end": "15-Dec-2021 at 5:00 PM",
      "building": "BRONFMAN",
      "room": "422",
      "row": "",
      "from": "AAA",
      "to": "KAA"
    }
  ]
}
```
