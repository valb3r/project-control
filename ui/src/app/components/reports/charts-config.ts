export class ChartsConfig {

  private static colors = ["#ff0beb","#7882ed","#1ca1f2","#9deac7","#66e8a8","#21ca3f","#7b9020","#bebd18","#fab47d","#e15b86"];

  static defaultBarChart(): any {
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      title: {
        left: 'center'
      },
      toolbox: {
        feature: {
          dataZoom: {
            yAxisIndex: 'none'
          },
          restore: {},
          saveAsImage: {}
        }
      },
      xAxis: {
        type: 'time',
        boundaryGap: false,
        data: [],
        axisTick: {
          alignWithLabel: true,
        },
      },
      yAxis: {
        type: 'value'
      },
      dataZoom: [{
        type: 'inside',
        start: 0,
        end: 100
      }, {
        start: 0,
        end: 100,
        handleSize: '80%',
      }],
      series: []
    }
  }

  static seriesColor(name: String): String {
    return this.colors[this.hash(name) % this.colors.length];
  }

  private static hash(value: String) {
    let hash = 0, i, chr;
    for (i = 0; i < value.length; i++) {
      chr   = value.charCodeAt(i);
      hash  = ((hash << 5) - hash) + chr;
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  }
}

