import datetime
import bs4 as bs
import sys
import urllib.request
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

    def Callable(self, html_str):
        self.html = html_str
        self.app.quit()


def main():
    page = Page('https://www.tms.pl/rynek/kursy-walut')
 
    soup = bs.BeautifulSoup(page.html, 'html.parser')
    table2 = soup.find('table', class_='tradings')
    if table2 is None : print("Did not connect")
    else : print("connected")
if __name__ == '__main__': main()
