# Dow Jones Visual/Auditory Model
This is a program written in Processing 4 using Java that allows users to create and manipulate a musical staff, where they can drag and drop music note icons. The music note icons represent companies in the Dow Jones Industrial Average, the vertical axis of the staff represents the time since the shares were purchased, and the horizontal axis represents the number of shares purchased. When a user drags a company onto the staff, it plays either a positive or negative music sound based on whether the company is doing well from that time point selected. The user has the ability to select how many shares purchased, because the staff can act as a mock portfolio when multiple companies are dragged onto the staff. Thus this program is both an informative and educational tool, as it encourages experimenting with different companies dates and amount of shares to intuitively learn more about companies' history in the Dow Jones. The program uses sound files and JSON data to determine the financial performance of the companies. The program also includes logic to prevent notes from overlapping on the staff. 

I created this model in the summer of 2022 as part of my internship with S&P Global. 
<br>
## [Demonstration](https://youtu.be/uLiaaL0SDmk)
[![Usage Example](https://img.youtube.com/vi/uLiaaL0SDmk/0.jpg)](https://youtu.be/uLiaaL0SDmk)

This is a demonstration I created of how I use this model to understand more about the history of a company's performance on the market. In this demonstration I'm particularly focused on examining how changing the position (adding more shares, or changing the purchase date) of APPL changes the net result of the overall portfolio. 

If you are interested in using this model, feel free to download the .pde files if you have Processing 4 or later installed on your computer. You just need to change the data source which should be in the format of a JSON file. In the *setup()* method of the code this is the lines: 
```
jsonCompanies = loadJSONObject("dow30_companies.json");
companyData = loadJSONObject("dow30_quotes.json");
```
The file name should be inside the quotes within the parenthesis. *companyData* is the variable that refers to the file that contains the stock data points, and *jsonCompanies* is the variable that refers to the corresponding company name to get the correct label for each music note icon.
