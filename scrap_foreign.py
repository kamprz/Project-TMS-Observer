import datetime
import bs4 as bs
import sys
from PyQt5.QtWebEngineWidgets import QWebEnginePage
from PyQt5.QtWidgets import QApplication
from PyQt5.QtCore import QUrl

class Page(QWebEnginePage):
    def __init__(self, url):
        self.app = QApplication(sys.argv)
        QWebEnginePage.__init__(self)
        self.html = ''
        self.loadFinished.connect(self._on_load_finished)
        self.load(QUrl(url))
        self.app.exec_()

    def _on_load_finished(self):
        self.html = self.toHtml(self.Callable)
        #print('Load finished')

    def Callable(self, html_str):
        self.html = html_str
        self.app.quit()


def main():
    currencies_list=[]

    url_foreign = 'https://www.tms.pl/kursy-walut/2'

    page2 = Page(url_foreign)
    soup2 = bs.BeautifulSoup(page2.html, 'html.parser')
    table2 = soup2.find('table', class_='tradings')
    currencies2 = table2.find_all('tr')
    for waluty in currencies2:
        wartosci = waluty.find_all("td")
        for item in wartosci:
            currencies_list.append(item.text+" ")

    for item in currencies_list:
        print(item,end=" ")
    exit(0)

if __name__ == '__main__': main()
